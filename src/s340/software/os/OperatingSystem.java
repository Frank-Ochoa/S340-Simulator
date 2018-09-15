package s340.software.os;

import s340.hardware.*;
import s340.hardware.exception.MemoryFault;

import java.util.LinkedList;
import java.util.List;

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
		// Create a wait process that continually jumps to itself
		ProgramBuilder pb = new ProgramBuilder();
		pb.start(0);
		pb.jmp(0);
		Program b1 = pb.build();
		List<Program> x = new LinkedList<>();
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
	private int loadProgram(Program program) throws MemoryFault
	{
		// Address = the start address of passed in program
		int address = program.getStart();
		for (int i : program.getCode())
		{
			machine.memory.store(address++, i);
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
			loadProgram(program);
			ProcessControlBlock x = new ProcessControlBlock(0, 0, program.getStart(), READY);

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
	@Override public synchronized void syscall(int savedProgramCounter, int callNumber)
	{
		//  leave this code here
		CheckValid.syscallNumber(callNumber);
		if (!machine.cpu.runProg)
		{
			return;
		}
		//  end of code to leave

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
}
