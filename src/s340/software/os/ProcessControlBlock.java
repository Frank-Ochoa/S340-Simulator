package s340.software.os;

public class ProcessControlBlock implements Comparable<ProcessControlBlock>
{
	int Acc;
	int X;
	int PC;
	ProcessState Status;
	int BASE;
	int LIMIT;

	public ProcessControlBlock(int PC, int BASE, int LIMIT)
	{
		this.Acc = 0;
		this.X = 0;
		this.PC = PC;
		this.Status = ProcessState.READY;
		this.BASE = BASE;
		this.LIMIT = LIMIT;
	}

	@Override public String toString()
	{
		return "ProcessControlBlock{" + "Acc=" + Acc + ", X=" + X + ", PC=" + PC + ", Status=" + Status + ", BASE="
				+ BASE + ", LIMIT=" + LIMIT + '}';
	}

	@Override public int compareTo(ProcessControlBlock o)
	{
		return this.BASE - o.BASE;
	}
}
