package s340.software.os;

import s340.hardware.*;
import s340.hardware.exception.MemoryFault;

import java.util.*;

import static s340.software.os.ProcessState.*;

/*
 * The operating system that controls the software running on the S340 CPU.
 *
 * The operating system acts as an interrupt handler, a system call handler, and
 * a trap handler.
 */
public class OperatingSystem implements IInterruptHandler, ISystemCallHandler, ITrapHandler
{
	private static final int PROCESS_TABLE_SIZE = 10;
	// the machine on which we are running.
	private final Machine machine;
	private ProcessControlBlock[] process_table;
	private int runningIndex;
	private List<FreeSpace> freeSpaces;
	private Deque<IORequest>[] waitQueues;
	private ICallables[] deviceMethods;

	/*
	 * Create an operating system on the given machine.
	 */
	public OperatingSystem(Machine machine) throws MemoryFault
	{
		this.machine = machine;
		// Initialize an array of process control blocks with the size of 10
		this.process_table = new ProcessControlBlock[PROCESS_TABLE_SIZE];
		// Initialize the running index to 0
		this.runningIndex = 0;
		// Initial free space is entire block of memory until a process is loaded in
		this.freeSpaces = new ArrayList<>();
		freeSpaces.add(new FreeSpace(0, Machine.MEMORY_SIZE));
		this.waitQueues = new Deque[Machine.NUM_DEVICES];
		for (int i = 0; i < waitQueues.length; i++)
		{
			waitQueues[i] = new LinkedList<>();
		}

		this.deviceMethods = new ICallables[Machine.NUM_DEVICES];
		deviceMethods[Machine.CONSOLE] = new ICallables()
		{
			@Override public void startDevice(Machine theMachine, IORequest request) throws MemoryFault
			{
				DeviceControlRegister controlRegister = theMachine.controlRegisters[Machine.CONSOLE];
				controlRegister.register[0] = request.getOperations();
				controlRegister.register[1] = request.getSourceProcess().Acc;
				controlRegister.latch();
			}

			@Override public void interruptPostProcessing(Machine theMachine, IORequest finishedProcess)
					throws MemoryFault
			{

			}

			@Override public IORequest getNextProcess(Machine theMachine, IORequest finishedProcess)
			{
				return waitQueues[Machine.CONSOLE].peek();
			}

		};
		deviceMethods[Machine.DISK1] = new ICallables()
		{
			@Override public void startDevice(Machine theMachine, IORequest request) throws MemoryFault
			{
				theMachine.memory.setBase(0);
				theMachine.memory.setLimit(Machine.MEMORY_SIZE);

				// Pass in IORequest to start device method
				// Need to use generic device number
				// Possibly replace this with the SFTS algoritm, b/c this is just what is selecting the next thing to do
				// and instead of first come first serve do the SFTS
				//IORequest request = waitQueues[deviceNumber].peek();

				DeviceControlRegister controlRegister = theMachine.controlRegisters[Machine.DISK1];
				controlRegister.register[0] = request.getOperations();

				// Uneeded but making explicit at the start
				int storedLocation = (request.getSourceProcess().Acc) + (request.getSourceProcess().BASE);
				// To Skip device #
				storedLocation++;
				int platterSize = theMachine.memory.load(storedLocation++);
				// Need this for SFTS
				int start = theMachine.memory.load(storedLocation++);
				// Need this for SFTS
				int length = theMachine.memory.load(storedLocation++);
				int startLoadLocation = theMachine.memory.load(storedLocation) + (request.getSourceProcess().BASE);

				controlRegister.register[1] = platterSize;
				controlRegister.register[2] = start;
				controlRegister.register[3] = length;

				if (request.getOperations() == DeviceControllerOperations.WRITE)
				{
					int address = 0;
					for (int i = startLoadLocation; i < startLoadLocation + length; i++)
					{
						int instruction = theMachine.memory.load(i);
						theMachine.devices[Machine.DISK1].buffer[address++] = instruction;
					}

					//System.out.println(Arrays.toString(theMachine.devices[deviceNumber].buffer));

				}

				controlRegister.latch();
			}

			@Override public void interruptPostProcessing(Machine theMachine, IORequest finishedProcess)
					throws MemoryFault
			{
				if (finishedProcess.getOperations() == DeviceControllerOperations.READ)
				{
					theMachine.memory.setBase(0);
					theMachine.memory.setLimit(Machine.MEMORY_SIZE);

					// Get the start location of where you will be storing in memory
					int startStoreLocation = theMachine.memory
							.load((finishedProcess.getSourceProcess().Acc + finishedProcess.getSourceProcess().BASE)
										  + 4);
					startStoreLocation += finishedProcess.getSourceProcess().BASE;

					int length = theMachine.memory
							.load((finishedProcess.getSourceProcess().Acc + finishedProcess.getSourceProcess().BASE)
										  + 3);

					for (int i = 0; i < length; i++)
					{
						theMachine.memory.store(startStoreLocation++, theMachine.devices[Machine.DISK1].buffer[i]);
					}

				}

			}

			@Override public IORequest getNextProcess(Machine theMachine, IORequest finishedProcess) throws MemoryFault
			{
				theMachine.memory.setBase(0);
				theMachine.memory.setLimit(Machine.MEMORY_SIZE);

				int storedLocation =
						(finishedProcess.getSourceProcess().Acc) + (finishedProcess.getSourceProcess().BASE);
				int start = theMachine.memory.load(storedLocation + 2);
				int length = theMachine.memory.load(storedLocation + 3);
				int headLocation = start + length;

				IORequest chosenRequest = null;

				int min = Integer.MAX_VALUE;
				for (IORequest nextRequest : waitQueues[Machine.DISK1])
				{
					int nextStoredLocation =
							(nextRequest.getSourceProcess().Acc) + (nextRequest.getSourceProcess().BASE);
					int nextHeadLocation = theMachine.memory.load(nextStoredLocation + 2);

					if (min > Math.abs(nextHeadLocation - headLocation))
					{
						min = Math.abs(nextHeadLocation - headLocation);
						chosenRequest = nextRequest;
					}

				}

				// Remove from queue and put it back on the head
				// Scan the Q for the chosenRequest, remove it, then add it back to the head of the Q
				Iterator<IORequest> it = waitQueues[Machine.DISK1].iterator();
				while (it.hasNext())
				{
					IORequest request = it.next();

					if (it.next().equals(chosenRequest))
					{
						it.remove();
						waitQueues[Machine.DISK1].addFirst(request);
						break;
					}
				}

				return chosenRequest;
			}

		};

		// Create a wait process that continually jumps to itself
		ProgramBuilder pb = new ProgramBuilder();
		pb.size(0);
		pb.jmp(0);
		Program b1 = pb.build();
		List<Program> x = new ArrayList<>();
		x.add(b1);
		// Load it into memory and store it into the process control table
		schedule(x);

	}

	private ProcessControlBlock chooseNextProcess()
	{
		// Loop through the process table starting at runningIndex + 1, searching for a process control block
		// with the status of READY. If one is found, set the runningIndex = to that index in the loop, and
		// return said block
		for (int i = runningIndex + 1; i < this.process_table.length; i++)
		{
			if (process_table[i] != null)
			{
				if (process_table[i].Status == READY)
				{
					runningIndex = i;
					return this.process_table[i];
				}
			}
		}

		// If no process was found with the READY status in the previous loop, loop through the process table once again
		// starting at position 1 till you reach the current runningIndex, searching for a process control block
		// with the status of READY, if one is found, set the runningIndex = to that index in the loop,
		// and return said block
		for (int i = 1; i <= runningIndex; i++)
		{
			if (process_table[i] != null)
			{
				if (process_table[i].Status == READY)
				{
					runningIndex = i;
					return this.process_table[i];
				}
			}
		}

		// No process control block exists in the process table with the status of READY,
		// thus set the runningIndex to 0, the index of the wait process, and return said wait process
		runningIndex = 0;
		return process_table[0];
	}

	/*
	 * Load a program into a given memory address
	 */
	// Still want this to load the programs
	private int loadProgram(Program program, int index) throws MemoryFault
	{
		// Address = the start address of passed in program
		// Load at free space start
		int address = 0;
		for (int i : program.getCode())
		{
			machine.memory.store(freeSpaces.get(index).getSTART() + address++, i);
		}

		return address;
	}

	/*
	 * Scheduled a list of programs to be run.
	 *
	 *
	 * @param programs the programs to schedule
	 */
	public synchronized void schedule(List<Program> programs) throws MemoryFault
	{
		// For each program in the passed in list, load it into memory, and create a process control block for it
		for (Program program : programs)
		{
			int instructionSize = program.getCode().length;
			int largestVirtualAddress = instructionSize + program.getDataSize();
			int iFS = allocateFreeSpace(largestVirtualAddress);

			loadProgram(program, iFS);
			ProcessControlBlock x = new ProcessControlBlock(0, freeSpaces.get(iFS).getSTART(), largestVirtualAddress);

			/*System.out.println("Program BASE is = " + freeSpaces.get(0).getSTART());
			System.out.println("Program LIMIT is = " + (x.LIMIT));
			System.out.println("--------------------------------");*/

			// Loop through the process control table looking for either a null block or block with the status of END, if one is
			// found, store the previously created process control block in the process table at that index
			for (int i = 0; i < this.process_table.length; i++)
			{
				if (this.process_table[i] == null || this.process_table[i].Status == END)
				{
					this.process_table[i] = x;
					break;
				}
			}

			/*for(int i = 0; i < this.process_table.length; i++)
			{
				System.out.println(this.process_table[i]);
			}*/

			// 0 - limit now no longer free
			freeSpaces.get(iFS).setSTART(x.BASE + x.LIMIT);
			freeSpaces.get(iFS).setLENGTH(freeSpaces.get(iFS).getLENGTH() - x.LIMIT);
			diagnostics();
		}

		// leave this as the last line
		machine.cpu.runProg = true;
	}

	// Save the registers into the correct, corresponding process control block
	private void saveRegisters(int PC)
	{
		process_table[runningIndex].Acc = machine.cpu.acc;
		process_table[runningIndex].X = machine.cpu.x;
		process_table[runningIndex].PC = PC;
	}

	// Restore the registers of the passed in process control block
	private void loadRegisters(ProcessControlBlock next)
	{
		machine.cpu.acc = next.Acc;
		machine.cpu.x = next.X;
		// Added BASE and LIMIT registers
		machine.memory.setBase(next.BASE);
		machine.memory.setLimit(next.LIMIT);
		machine.cpu.setPc(next.PC);
	}

	/*
	 * Handle a trap from the hardware.
	 *
	 * @param programCounter -- the program counter of the instruction after the
	 * one that caused the trap.
	 *
	 * @param trapNumber -- the trap number for this trap.
	 */
	@Override public synchronized void trap(int savedProgramCounter, int trapNumber)
	{
		//  leave this code here
		CheckValid.trapNumber(trapNumber);
		if (!machine.cpu.runProg)
		{
			return;
		}
		//  end of code to leave

		// Entered the trap handler, save registers of current program
		//System.out.println("Base in trap = " + machine.memory.getBase() + " with " + runningIndex + " runningIndex");
		saveRegisters(savedProgramCounter);

		switch (trapNumber)
		{
			case Trap.TIMER:
				// Set current running process back READY status, since its not yet over
				process_table[runningIndex].Status = READY;
				break;
			// Program end, or did division by 0, set the status of that process control block to end
			case Trap.END:
			case Trap.DIV_ZERO:
				process_table[runningIndex].Status = END;
				freeSpaces.add(new FreeSpace(process_table[runningIndex].BASE,
											 process_table[runningIndex].BASE + process_table[runningIndex].LIMIT));
				System.out.println("Program ended, added a space" + freeSpaces);
				mergedSpaces();
				break;
			default:
				System.err.println("UNHANDLED TRAP " + trapNumber);
				System.exit(1);
		}

		// Choose the next process to be run
		ProcessControlBlock next = chooseNextProcess();
		//System.out.println(next);
		// Set the status of that process control block to RUNNING
		next.Status = RUNNING;
		// Restore registers for that process control block and jump to it
		loadRegisters(next);
	}

	/*
	 * Handle a system call from the software.
	 *
	 * @param programCounter -- the program counter of the instruction after the
	 * one that caused the trap.
	 *
	 * @param callNumber -- the callNumber of the system call.
	 *
	 * @param address -- the memory address of any parameters for the system
	 * call.
	 */
	@Override public synchronized void syscall(int savedProgramCounter, int callNumber) throws MemoryFault
	{
		//  leave this code here
		CheckValid.syscallNumber(callNumber);
		if (!machine.cpu.runProg)
		{
			return;
		}
		//  end of code to leave

		saveRegisters(savedProgramCounter);

		// Make these make IORequests and q them, and start device
		switch (callNumber)
		{
			case SystemCall.REQUEST_MORE_MEMORY:
				// Load into ACC how much more memory program is requesting and pass to sbrk() method
				sbrk(process_table[runningIndex].Acc);
				break;
			case SystemCall.WRITE_TO_CONSOLE:
				queueOrStartIO(DeviceControllerOperations.WRITE, Machine.CONSOLE);
				chooseAndJumpNextProcess();
				diagnostics();
				break;
			case SystemCall.READ_FROM_DISK:
				queueOrStartIO(DeviceControllerOperations.READ,
							   this.machine.memory.load(process_table[runningIndex].Acc));
				chooseAndJumpNextProcess();
				break;
			case SystemCall.WRITE_TO_DISK:
				queueOrStartIO(DeviceControllerOperations.WRITE,
							   this.machine.memory.load(process_table[runningIndex].Acc));
				chooseAndJumpNextProcess();
				break;
			default:
				System.err.println("UNHANDLED SYSCALL " + callNumber);
				System.exit(1);
		}

		diagnostics();

		loadRegisters(process_table[runningIndex]);
	}

	/*
	 * Handle an interrupt from the hardware.
	 *
	 * @param programCounter -- the program counter of the instruction after the
	 * one that caused the trap.
	 *
	 * @param deviceNumber -- the device number that is interrupting.
	 */
	@Override public synchronized void interrupt(int savedProgramCounter, int deviceNumber) throws MemoryFault
	{
		//  leave this code here
		CheckValid.deviceNumber(deviceNumber);
		if (!machine.cpu.runProg)
		{
			return;
		}
		//  end of code to leave
		// Need to update head in here?

		// Clear ICR
		this.machine.interruptRegisters.register[deviceNumber] = false;
		// Save Registers
		saveRegisters(savedProgramCounter);

		// Set Process that finished its IO back to READY
		// Dequeue it
		IORequest finishedProcess = waitQueues[deviceNumber].remove();
		finishedProcess.getSourceProcess().Status = READY;

		// Do Post Processing when appropriate
		deviceMethods[deviceNumber].interruptPostProcessing(this.machine, finishedProcess);

		// If queue is not empty, start the next IO
		if (!waitQueues[deviceNumber].isEmpty())
		{
			IORequest nextRequest = deviceMethods[deviceNumber].getNextProcess(this.machine, finishedProcess);

			deviceMethods[deviceNumber].startDevice(this.machine, nextRequest);
		}

		//diagnostics();
		// Restore registers and jump back to running process
		loadRegisters(process_table[runningIndex]);
	}

	private int allocateFreeSpace(int size)
	{
		machine.memory.setLimit(Machine.MEMORY_SIZE);

		// Scan list of free spaces looking for 1st big enough space for program size, return its index if found
		for (int i = 0; i < this.freeSpaces.size(); i++)
		{
			if (this.freeSpaces.get(i).getLENGTH() >= size)
			{
				// Set Limit in memory to highest limit of free space. Length + its Starting position
				return i;
			}
		}

		System.err.println("Error: Not enough Memory");
		System.exit(1);
		return -1;
	}

	private boolean expandInPlace(ProcessControlBlock process, int wantedSpace)
	{
		// Loop through free spaces
		for (int i = 0; i < freeSpaces.size(); i++)
		{
			// If there is an adjacent free space
			if ((process.LIMIT + process.BASE) == freeSpaces.get(i).getSTART())
			{
				// If aforementioned free space is big enough
				if (freeSpaces.get(i).getLENGTH() >= wantedSpace)
				{
					// Allocate more memory to program and alter the frees space appropriately
					process.LIMIT = process.LIMIT + wantedSpace;
					freeSpaces.get(i).setSTART(freeSpaces.get(i).getSTART() + wantedSpace);
					freeSpaces.get(i).setLENGTH(freeSpaces.get(i).getLENGTH() - wantedSpace);

					if (freeSpaces.get(i).getLENGTH() == 0)
					{
						freeSpaces.remove(i);
						System.out.println("Removed a space" + freeSpaces);
					}

					// sbrk worked load 0 into the accumulator
					process.Acc = 0;
					return true;
				}
			}

		}

		return false;
	}

	private boolean moveProgram(ProcessControlBlock process, int wantedSpace) throws MemoryFault
	{
		// still set as the running index, so need to set them back to 0 and total mem size
		machine.memory.setBase(0);
		machine.memory.setLimit(Machine.MEMORY_SIZE);

		int address = 0;
		// Loop through free spaces
		for (int i = 0; i < freeSpaces.size(); i++)
		{
			// If there is a big enough free space to accomate the program + space it requested
			if (freeSpaces.get(i).getLENGTH() >= (wantedSpace + process.LIMIT))
			{
				// Loop through its instructions
				for (int b = process.BASE; b < (process.LIMIT + process.BASE); b++)
				{
					// Load instructions of start of program
					int instruction = machine.memory.load(b);
					// Store at free space
					machine.memory.store(freeSpaces.get(i).getSTART() + address++, instruction);
				}

				// Add a freeSpace where program had been
				freeSpaces.add(new FreeSpace(process.BASE, process.BASE + process.LIMIT));

				// Alter the registers to appropriate values
				process.BASE = freeSpaces.get(i).getSTART();

				freeSpaces.get(i).setSTART(freeSpaces.get(i).getSTART() + (wantedSpace + process.LIMIT));
				freeSpaces.get(i).setLENGTH(freeSpaces.get(i).getLENGTH() - (wantedSpace + process.LIMIT));

				process.LIMIT = process.LIMIT + wantedSpace;

				if (freeSpaces.get(i).getLENGTH() == 0)
				{
					freeSpaces.remove(i);
				}

				// sbrk worked, load 0 into the accumulator
				process.Acc = 0;
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("Duplicates") private boolean mergedSpaces()
	{
		boolean didMerge = false;

		// If freeSpaces is not empty
		if (!freeSpaces.isEmpty())
		{
			// Sort the free spaces
			Collections.sort(freeSpaces);
			System.out.println("Sorted the freespaces" + freeSpaces);

			// Iterate over them
			Iterator<FreeSpace> it = freeSpaces.iterator();
			FreeSpace space = it.next();

			while (it.hasNext())
			{
				FreeSpace spaceNext = it.next();
				// If there is an adjacent free space
				if (space.getLENGTH() + space.getSTART() == spaceNext.getSTART())
				{
					// Merge them together
					space.setLENGTH(space.getLENGTH() + spaceNext.getLENGTH());
					// Delete free space to the right
					it.remove();
					didMerge = true;
				}
				else
				{
					space = spaceNext;
				}
			}
		}

		System.out.println("Merged Spaces: " + freeSpaces);
		return didMerge;
	}

	private boolean memoryCompaction(ProcessControlBlock process, int wantedSpace) throws MemoryFault
	{
		System.out.println("Diagnostic: Memory Compaction called");

		// still set as the running index, so need to set them back to 0 and total mem size
		machine.memory.setBase(0);
		machine.memory.setLimit(Machine.MEMORY_SIZE);

		List<ProcessControlBlock> blockList = new ArrayList<>();

		// Loop through adding correct PCBS to blockList
		for (int i = 1; i < process_table.length; i++)
		{
			if (process_table[i] != null && process_table[i].Status != END)
			{
				blockList.add(process_table[i]);
			}
		}

		// Sort that dirty blockList
		Collections.sort(blockList);
		System.out.println("Ordered PCBS: " + blockList);

		// Find index in blockList of proccess calling sbrk
		int index = -1;
		for (int i = 0; i < blockList.size(); i++)
		{
			if (blockList.get(i).BASE == process.BASE)
			{
				index = i;
				break;
			}
		}

		//System.out.println("INDEX IS: " + index);

		if (index != -1)
		{
			// Move everything in blockList <= index left

			int addressLeft = process_table[0].LIMIT;
			for (int i = 0; i <= index; i++)
			{
				int processBASE = blockList.get(i).BASE;
				int processLIMIT = blockList.get(i).LIMIT;
				blockList.get(i).BASE = addressLeft;

				//System.out.println("Diagnostic: start of move left");
				for (int b = processBASE; b < (processBASE + processLIMIT); b++)
				{
					//System.out.println("b is: " + b);
					//System.out.println("addressLeft is : " + addressLeft);
					// Load instructions of start of program
					//System.out.println("Diagnostic: before load");
					int instruction = machine.memory.load(b);
					//System.out.println(instruction);
					//System.out.println("Diagnostic: after load");
					// Store at free space
					machine.memory.store(addressLeft++, instruction);
				}
				//System.out.println("Diagnostic: end of move left");
			}

			//Move everything in blockList > index right
			int addressRight = Machine.MEMORY_SIZE - 1;
			for (int i = blockList.size() - 1; i > index; i--)
			{
				int processBASE = blockList.get(i).BASE;
				int processLIMIT = blockList.get(i).LIMIT;
				blockList.get(i).BASE = (addressRight - processLIMIT) + 1;

				//System.out.println("Diagnostic: start of move right");
				// Pretty sure this should be b = that - 1 ? But its not outputting
				for (int b = (processBASE + processLIMIT) - 1; b >= processBASE; b--)
				{
					//System.out.println("b is: " + b);
					//System.out.println("addressRight is : " + addressRight);
					// Load instructions of start of program
					//System.out.println("Diagnostic: before load");
					int instruction = machine.memory.load(b);
					//System.out.println(instruction);
					//System.out.println("Diagnostic: after load");
					// Store at free space
					machine.memory.store(addressRight--, instruction);
				}
				//System.out.println("Diagnostic: end of move right");
			}
		}
		else
		{
			return false;
		}

		// Create a new list to represent free spaces and add the appropriate free space
		List<FreeSpace> newFreeSpaces = new ArrayList<>();
		if ((blockList.get(blockList.size() - 1).LIMIT + blockList.get(blockList.size() - 1).BASE)
				== Machine.MEMORY_SIZE)
		{
			newFreeSpaces.add(new FreeSpace(blockList.get(index).LIMIT + blockList.get(index).BASE,
											blockList.get(index + 1).BASE));
		}
		else
		{
			newFreeSpaces
					.add(new FreeSpace(blockList.get(index).LIMIT + blockList.get(index).BASE, Machine.MEMORY_SIZE));
		}

		// Point freeSpaces at the new list
		freeSpaces = newFreeSpaces;
		//System.out.println("Diagnostic: Memory Compaction Ended");
		//diagnostics();
		//System.out.println("Freespaces after memory compaction: " + freeSpaces);

		// Attempt to expand in place
		return expandInPlace(process, wantedSpace);
	}

	private boolean sbrk(int wantedSpace) throws MemoryFault
	{
		ProcessControlBlock process = this.process_table[runningIndex];

		// Attempt to expand in place
		if (expandInPlace(process, wantedSpace))
		{
			System.out.println("Successfully expanded in place");
			return true;
		}

		// Attempt to move the program
		if (moveProgram(process, wantedSpace))
		{
			System.out.println("Successfully moved a program");
			return true;
		}

		// Else scan list of free spaces looking for adjacent free and merging them together, (Perform merging)
		// Now check if you can expand in place or move the program with the newly merged free spaces
		if (mergedSpaces())
		{
			if (expandInPlace(process, wantedSpace))
			{
				System.out.println("Successfully expanded in place");
				return true;
			}

			if (moveProgram(process, wantedSpace))
			{
				System.out.println("Successfully moved a program place");
				return true;
			}
		}
		// Else attempt to perform memory compaction
		if (memoryCompaction(process, wantedSpace))
		{
			return true;
		}
		// Else nothing worked, not enough memory available and exit
		else
		{
			System.err.println("Error: Not enough memory availible");
			System.exit(1);
		}

		return false;
	}

	private void diagnostics()
	{
		StringBuilder s = new StringBuilder();
		s.append("------------------------------------").append("\n");
		s.append("START OF DIAGNOSTIC").append("\n\n");
		for (int i = 0; i < process_table.length; i++)
		{
			if (process_table[i] != null && process_table[i].Status != END)
			{
				s.append("Process #: ").append(i).append("\n");
				s.append("Base: ").append(process_table[i].BASE).append("\n");
				s.append("Limit: ").append(process_table[i].LIMIT).append("\n\n");
			}

		}

		for (FreeSpace x : freeSpaces)
		{
			s.append(x).append("\n");
		}

		for(int i = 0; i < Machine.NUM_DEVICES; i++)
		{
			switch(i)
			{
				case 0:
					s.append("\n").append("KEYBOARD WAIT QUEUE: ");
					break;
				case 1:
					s.append("\n").append("CONSOLE WAIT QUEUE: ");
					break;
				case 2:
					s.append("\n").append("DISK ONE WAIT QUEUE: ");
					break;
				case 3:
					s.append("\n").append("DISK TWO WAIT QUEUE: ");
					break;
				default:
					System.out.println("DEVICE WAIT QUEUE NOT FOUND");
			}

			for(IORequest x : waitQueues[i])
			{
				s.append(x);
			}
		}

		s.append("\n").append("\n").append("END OF DIAGNOSTIC").append("\n");
		s.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~").append("\n");

		System.out.print(s);

	}

	private void chooseAndJumpNextProcess()
	{
		ProcessControlBlock next = chooseNextProcess();
		next.Status = RUNNING;
		loadRegisters(next);
	}

	private void queueOrStartIO(int operation, int deviceNumber) throws MemoryFault
	{
		IORequest request = new IORequest(operation, process_table[runningIndex]);
		waitQueues[deviceNumber].add(request);
		request.getSourceProcess().Status = WAITING;

		if (waitQueues[deviceNumber].size() == 1)
		{
			deviceMethods[deviceNumber].startDevice(this.machine, waitQueues[deviceNumber].peek());
		}

	}
}

