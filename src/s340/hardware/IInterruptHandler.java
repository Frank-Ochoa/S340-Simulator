package s340.hardware;

import s340.hardware.exception.MemoryFault;

/*
 * An interrupt handler.
 */
public interface IInterruptHandler
{
	void interrupt(int savedProgramCounter, int deviceNumber) throws MemoryFault;
}
