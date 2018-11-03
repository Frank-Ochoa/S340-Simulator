package s340.software.os;

import s340.hardware.Machine;
import s340.hardware.exception.MemoryFault;

public interface ICallables
{
	void startDevice(Machine theMachine) throws MemoryFault;

	void interruptPostProcessing(Machine theMachine, ProcessControlBlock finishedProcess) throws MemoryFault;
}
