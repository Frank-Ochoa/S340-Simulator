package s340.software.os;

import s340.hardware.MemoryController;

public class ProcessControlBlock implements Comparable<ProcessControlBlock>
{
	int Acc;
	int X;
	int PC;
	ProcessState Status;
	int BASE;
	int LIMIT;

	public ProcessControlBlock(int acc, int x, int PC, ProcessState status, int BASE, int LIMIT)
	{
		this.Acc = acc;
		this.X = x;
		this.PC = PC;
		this.Status = status;
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
