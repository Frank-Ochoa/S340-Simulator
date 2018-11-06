package s340.software.os;

import s340.hardware.Machine;
import s340.hardware.exception.MemoryFault;

public interface ICallables
{
	void startDevice(Machine theMachine, int deviceNumber) throws MemoryFault;

	void interruptPostProcessing(Machine theMachine, IORequest finishedProcess) throws MemoryFault;
}
