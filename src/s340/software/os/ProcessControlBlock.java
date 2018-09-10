package s340.software.os;

public class ProcessControlBlock
{
	int Acc;
	int X;
	int PC;
	ProcessState Status;

	public ProcessControlBlock(int acc, int x, int PC, ProcessState status)
	{
		Acc = acc;
		X = x;
		PC = PC;
		Status = status;
	}


}
