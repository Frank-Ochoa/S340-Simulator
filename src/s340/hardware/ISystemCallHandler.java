package s340.hardware;

import s340.hardware.exception.MemoryFault;

/*
 * A system call handler.
 */
public interface ISystemCallHandler
{
	void syscall(int savedProgramCounter, int callNumber) throws MemoryFault;
}
