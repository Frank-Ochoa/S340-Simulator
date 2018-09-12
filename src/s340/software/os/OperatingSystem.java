package s340.software.os;

import java.rmi.AlreadyBoundException;
import java.util.LinkedList;
import java.util.List;
import s340.hardware.IInterruptHandler;
import s340.hardware.ISystemCallHandler;
import s340.hardware.ITrapHandler;
import s340.hardware.Machine;
import s340.hardware.Trap;
import s340.hardware.exception.MemoryFault;

import static s340.software.os.ProcessState.END;
import static s340.software.os.ProcessState.READY;
import static s340.software.os.ProcessState.RUNNING;

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
	private int blockIndex;

    /*
	 * Create an operating system on the given machine.
     */
    public OperatingSystem(Machine machine) throws MemoryFault
    {
        this.machine = machine;
        this.process_table =  new ProcessControlBlock[10];
        this.runningIndex = 0;
        this.blockIndex = 1;
        ProgramBuilder pb = new ProgramBuilder();
       	pb.start(0);
        pb.jmp(0);
        pb.end();
        Program b1 = pb.build();
        process_table[0] = new ProcessControlBlock(0, 0, b1.getStart(), READY);


	}

    // The scheduler needs to call this?
    /*private ProcessControlBlock chooseNextProcess()
	{	ProcessControlBlock nextProcess = this.process_table[0];
		int tempRunningIndex = runningIndex + 1;
		boolean t = false;


		do
		{
			if(runningIndex == this.process_table.length)
			{
				runningIndex = 1;
			}

			if(tempRunningIndex == runningIndex)
			{
				runningIndex = 0;
				return nextProcess;
			}

			if(this.process_table[tempRunningIndex] != null)
			{
				if(this.process_table[tempRunningIndex].Status == READY)
				{
					nextProcess = this.process_table[tempRunningIndex];
					runningIndex = tempRunningIndex;
					t = true;
				}
			}

			tempRunningIndex++;


		}while(t == false);

		return nextProcess;
	}*/

    // What I did, and seems pretty correct
    private ProcessControlBlock chooseNextProcess()
	{
		ProcessControlBlock next = this.process_table[0];

		if(runningIndex == this.process_table.length - 1)
		{
			runningIndex = 0;
		}

		for(int i = runningIndex + 1; i < this.process_table.length; i++)
		{
			if(process_table[i] != null)
			{
				if(process_table[i].Status == READY)
				{
					next = this.process_table[i];
					runningIndex = i;
					return next;
				}
			}
		}

		runningIndex = 0;
		return next;
	}

    /*
	 * Load a program into a given memory address
     */
    private int loadProgram(Program program) throws MemoryFault
    {
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
    	// Have a running index instead possibly, so that is a program calls schedule, doesn't overwrite blocks
    	//int index = 0;
		// searh for an open control block to reuse if needed
        for (Program program : programs)
        {
            loadProgram(program);
            ProcessControlBlock x = new ProcessControlBlock(0, 0, program.getStart(), READY);

			if(blockIndex == this.process_table.length - 1)
			{
				blockIndex = 1;
			}

			for(int i = blockIndex; i < this.process_table.length; i++)
			{
				// Search for an open block, and put the block there, setting the block index to that
				if(this.process_table[i] == null || this.process_table[i].Status == END)
				{
					this.process_table[i] = x;
					blockIndex = i;
					break;
				}
			}
        }

        // leave this as the last line
        machine.cpu.runProg = true;
    }

    private void saveRegisters(int PC)
	{
		process_table[runningIndex].Acc = machine.cpu.acc;
		process_table[runningIndex].X = machine.cpu.x;
		process_table[runningIndex].PC = PC;
	}

	private void loadRegisters()
	{
		ProcessControlBlock next = chooseNextProcess();
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
    @Override
    public synchronized void trap(int savedProgramCounter, int trapNumber)
    {
        //  leave this code here
        CheckValid.trapNumber(trapNumber);
        if (!machine.cpu.runProg)
        {
            return;
        }
        //  end of code to leave

        switch (trapNumber)
        {
        	// Entered the trap handler
            case Trap.TIMER:
            	saveRegisters(savedProgramCounter);
				loadRegisters();
				break;
            // Program end
            case Trap.END:
            	saveRegisters(savedProgramCounter);
            	process_table[runningIndex].Status = END;
            	System.out.println("Program ended");
			    loadRegisters();
                break;
            default:
                System.err.println("UNHANDLED TRAP " + trapNumber);
                System.exit(1);
        }
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
    @Override
    public synchronized void syscall(int savedProgramCounter, int callNumber)
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
    @Override
    public synchronized void interrupt(int savedProgramCounter, int deviceNumber)
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
