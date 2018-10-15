package s340.software.os;

public class FreeSpace implements Comparable<FreeSpace>
{
	private int START;
	private int LENGTH;

	FreeSpace(int START, int END)
	{
		this.START = START;
		this.LENGTH = END - START;
	}

	int getSTART()
	{
		return START;
	}

	int getLENGTH()
	{
		return LENGTH;
	}

	void setSTART(int START)
	{
		this.START = START;
	}

	void setLENGTH(int programLength)
	{
		this.LENGTH = programLength;
	}

	@Override public int compareTo(FreeSpace o)
	{
		return (this.getSTART() - o.getSTART());
	}

	@Override public String toString()
	{
		return "FreeSpace{" + "START=" + START + ", LENGTH=" + LENGTH + '}';
	}
}
