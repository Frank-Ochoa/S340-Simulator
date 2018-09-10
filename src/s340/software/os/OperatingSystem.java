package s340.software.os;

import java.rmi.AlreadyBoundException;
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
	ProcessControlBlock[] process_table;
	int runningIndex;
	int blockIndex;

    /*
	 * Create an operating system on the given machine.
     */
    public OperatingSystem(Machine machine) throws MemoryFault
    {
        this.machine = machine;
        this.process_table =  new ProcessControlBlock[10];
        this.runningIndex = 0;
        this.blockIndex = 0;
        ProgramBuilder pb = new ProgramBuilder();
       	pb.start(0);
        pb.jmp(0);
        pb.end();
       	loadProgram(pb.build());


	}

    // The scheduler needs to call this?
    private ProcessControlBlock chooseNextProcess()
	{
		// Need to change this, start off at 1?
		ProcessControlBlock readyBlock = null;
		for(int i = 0; i < this.process_table.length; i++)
		{
			if(this.process_table[i].Status == READY)
			{
				readyBlock = this.process_table[i];
				runningIndex = i;
			}
		}
		return readyBlock;
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
    	// Have a running index instead possibly
    	//int index = 0;
        for (Program program : programs)
        {
            loadProgram(program);
            ProcessControlBlock x = new ProcessControlBlock(0, 0, program.getStart(), READY);
            this.process_table[blockIndex] = x;
            blockIndex++;
        }

        // leave this as the last line
        machine.cpu.runProg = true;
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

		// Timer turns off

		// Save registers to process table
		int Acc = machine.cpu.acc;
        int X = machine.cpu.x;
        int PC = savedProgramCounter;
        this.process_table[runningIndex] = new ProcessControlBlock(Acc, X, PC, END);

        // Choose Program to run next,restore its registers and jump to it in memory
		ProcessControlBlock next = chooseNextProcess();
		machine.cpu.acc = next.Acc;
		machine.cpu.x = next.X;
		machine.cpu.setPc(next.PC);

		// Turn timer back on

        switch (trapNumber)
        {
            case Trap.TIMER:
                break;
            case Trap.END:
            	// chooseNextProcess
                System.exit(1);
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
