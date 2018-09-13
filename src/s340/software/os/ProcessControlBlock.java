package s340.software.os;

public class ProcessControlBlock
{
	int Acc;
	int X;
	int PC;
	ProcessState Status;

	public ProcessControlBlock(int acc, int x, int PC, ProcessState status)
	{
		this.Acc = acc;
		this.X = x;
		this.PC = PC;
		this.Status = status;
	}

}
