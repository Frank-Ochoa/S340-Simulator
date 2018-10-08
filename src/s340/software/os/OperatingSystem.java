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
	// the machine on which we are running.
	private final Machine machine;
	private ProcessControlBlock[] process_table;
	private int runningIndex;
	private List<FreeSpace> freeSpaces;
	private List<ProcessControlBlock> blockList;

	/*
	 * Create an operating system on the given machine.
	 */
	public OperatingSystem(Machine machine) throws MemoryFault
	{
		this.machine = machine;
		// Initialize an array of process control blocks with the size of 10
		this.process_table = new ProcessControlBlock[10];
		// Initialize the running index to 0
		this.runningIndex = 0;
		// Initial free space is entire block of memory until a process is loaded in
		this.freeSpaces = new ArrayList<>();
		freeSpaces.add(new FreeSpace(0, Machine.MEMORY_SIZE));
		this.blockList = new LinkedList<>();

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
			ProcessControlBlock x = new ProcessControlBlock(0, 0, 0, READY, freeSpaces.get(iFS).getSTART(),
					largestVirtualAddress + 1);

			System.out.println("Program BASE is = " + freeSpaces.get(0).getSTART());
			System.out.println("Program LIMIT is = " + (x.LIMIT));
			System.out.println("--------------------------------");

			// Loop through the process control table looking for either a null block or block with the status of END, if one is
			// found, store the previously created process control block in the process table at that index
			for (int i = 0; i < this.process_table.length; i++)
			{
				if (this.process_table[i] == null || this.process_table[i].Status == END)
				{
					this.process_table[i] = x;
					this.blockList.add(x);
					break;
				}
			}

			/*for(int i = 0; i < this.process_table.length; i++)
			{
				System.out.println(this.process_table[i]);
			}*/

			// 0 - limit now no longer free
			freeSpaces.get(iFS).setSTART(x.BASE + x.LIMIT);
			freeSpaces.get(iFS).setLENGTH(x.LIMIT);
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

		switch (callNumber)
		{
			case SystemCall.REQUEST_MORE_MEMORY:
				// Load into ACC how much more memory program is requesting and pass to sbrk() method
				sbrk(machine.cpu.acc);
				break;
			default:
				System.err.println("UNHANDLED SYSCALL " + callNumber);
				System.exit(1);
		}
	}

	/*
	 * Handle an interrupt from the hardware.
	 *
	 * @param programCounter -- the program counter of the instruction after the
	 * one that caused the trap.
	 *
	 * @param deviceNumber -- the device number that is interrupting.
	 */
	@Override public synchronized void interrupt(int savedProgramCounter, int deviceNumber)
	{
		//  leave this code here
		CheckValid.deviceNumber(deviceNumber);
		if (!machine.cpu.runProg)
		{
			return;
		}
		//  end of code to leave
	}

	// Project 2 scratch work

	private int allocateFreeSpace(int size)
	{
		// Scan list of free spaces looking for 1st big enough space for program size, return its index if found
		for (int i = 0; i < this.freeSpaces.size(); i++)
		{
			if (this.freeSpaces.get(i).getLENGTH() >= size)
			{
				// Set Limit in memory to highest limit of free space. Length + its Starting position
				machine.memory.setLimit(Machine.MEMORY_SIZE);
				return i;
			}
		}

		System.exit(1);
		return -1;
	}

	private boolean expandInPlace(ProcessControlBlock process, int wantedSpace)
	{
		for (int i = 0; i < freeSpaces.size(); i++)
		{
			if ((process.LIMIT + process.BASE) == freeSpaces.get(i).getSTART())
			{
				if (freeSpaces.get(i).getLENGTH() >= wantedSpace)
				{
					process.LIMIT = process.LIMIT + wantedSpace;
					freeSpaces.get(i).setSTART(freeSpaces.get(i).getSTART() + wantedSpace);
					freeSpaces.get(i).setLENGTH(wantedSpace);

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
		int address = 0;
		for (int i = 0; i < freeSpaces.size(); i++)
		{
			if (freeSpaces.get(i).getLENGTH() >= wantedSpace + process.LIMIT)
			{
				for (int b = process.BASE; b < (process.LIMIT + process.BASE); b++)
				{
					// Load instructions of start of program
					int instruction = machine.memory.load(b);
					// Store at free space
					machine.memory.store(freeSpaces.get(i).getSTART() + address++, instruction);
				}

				// Maybe refactor in a method to do this
				freeSpaces.get(i).setSTART(freeSpaces.get(i).getSTART() + wantedSpace);
				freeSpaces.get(i).setLENGTH(wantedSpace);

				if (freeSpaces.get(i).getLENGTH() == 0)
				{
					freeSpaces.remove(i);
					System.out.println("Removed a space" + freeSpaces);
				}

				freeSpaces.add(new FreeSpace(process.BASE, process.LIMIT));
				System.out.println("Added a space" + freeSpaces);

				process.LIMIT = process.LIMIT + wantedSpace;
				process.BASE = freeSpaces.get(i).getSTART();

				// sbrk worked, load 0 into the accumulator
				process.Acc = 0;
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("Duplicates") private boolean mergedSpaces()
	{
		// Put in check to make sure things are in freespaces

		Collections.sort(freeSpaces);
		System.out.println("Sorted the freespaces" + freeSpaces);

		boolean didMerge = false;
		Iterator<FreeSpace> it = freeSpaces.iterator();
		FreeSpace space = it.next();

		while (it.hasNext())
		{
			FreeSpace spaceNext = it.next();
			if (space.getLENGTH() + space.getSTART() == spaceNext.getSTART())
			{
				space.setLENGTHLITERAL(space.getLENGTH() + spaceNext.getLENGTH());
				it.remove();
				didMerge = true;
			}
			else
			{
				space = spaceNext;
			}
		}

		return didMerge;
	}

	private boolean memoryCompaction(ProcessControlBlock process, int wantedSpace) throws MemoryFault
	{
		Collections.sort(blockList);
		Collections.sort(freeSpaces);

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

		if(index != -1)
		{
			// Move everything in blockList <= index left
			int addressLeft = 0;
			for (int i = 0; i <= index; i++)
			{
				for (int j = 0; j < process_table.length; j++)
				{
					if (blockList.get(i).BASE == process_table[j].BASE)
					{
						for (int k = 0; k < freeSpaces.size(); k++)
						{
							if (process_table[j].BASE == (freeSpaces.get(k).getSTART() + freeSpaces.get(k).getLENGTH()))
							{
								for (int b = process_table[j].BASE; b < (process_table[j].BASE + process_table[j].LIMIT); b++)
								{
									// Load instructions of start of program
									int instruction = machine.memory.load(b);
									// Store at free space
									machine.memory.store(freeSpaces.get(k).getSTART() + addressLeft++, instruction);
								}

								process_table[j].BASE = freeSpaces.get(k).getSTART();

								freeSpaces.get(k).setSTART(freeSpaces.get(k).getSTART() + process_table[j].LIMIT);

								break;
							}
						}

						break;
					}
				}
			}

			//Move everything in blockList > index right
			int addressRight = 0;
			for (int i = blockList.size() - 1; i > index; i--)
			{
				for (int j = 0; j < process_table.length; j++)
				{
					if (blockList.get(i).BASE == process_table[j].BASE)
					{
						for (int k = 0; k < freeSpaces.size(); k++)
						{
							if ((process_table[j].BASE + process_table[j].LIMIT) == freeSpaces.get(k).getSTART())
							{
								for (int b = (process_table[i].BASE + process_table[i].LIMIT) - 1; b >= process_table[i].BASE; b--)
								{
									// Load instructions of start of program
									int instruction = machine.memory.load(b);
									// Store at free space
									machine.memory.store((freeSpaces.get(k).getSTART() + freeSpaces.get(k).getLENGTH())
											- addressRight++, instruction);
								}

								process_table[i].BASE = process_table[i].BASE + freeSpaces.get(k).getLENGTH();

								freeSpaces.get(k).setSTART(process_table[i].BASE - freeSpaces.get(k).getLENGTH());

								break;
							}
						}

						break;
					}
				}
			}
		}
		else
		{
			return false;
		}

		//Merge free spaces
		mergedSpaces();

		// Attempt to expand in place
		return expandInPlace(process, wantedSpace);
	}

	private boolean sbrk(int wantedSpace) throws MemoryFault
	{
		ProcessControlBlock process = this.process_table[runningIndex];

		// Attempt to expand in place
		if (expandInPlace(process, wantedSpace))
		{
			return true;
		}

		// Attempt to move the program
		if (moveProgram(process, wantedSpace))
		{
			return true;
		}

		// Else scan list of free spaces looking for adjacent free and merging them together, (Perform merging)
		// Now check if you can expand in place or move the program with the newly merged free spaces
		if (mergedSpaces())
		{
			if (expandInPlace(process, wantedSpace))
			{
				return true;
			}

			if (moveProgram(process, wantedSpace))
			{
				return true;
			}
		}
		// Else attempt to perform memory compaction
		if (memoryCompaction(process, wantedSpace))
		{
			return true;
		}
		else
		{
			System.err.println("Error: Not enough memory availible");
			System.exit(1);
		}



		return false;
	}
}
