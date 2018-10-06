package s340.software.os;

public class FreeSpace implements Comparable<FreeSpace>
{
	private int START;
	private int LENGTH;

	public FreeSpace(int START, int END)
	{
		this.START = START;
		this.LENGTH = END - START;
	}

	public int getSTART()
	{
		return START;
	}

	public int getLENGTH()
	{
		return LENGTH;
	}

	public void setSTART(int START)
	{
		this.START = START;
	}

	public void setLENGTH(int programLength)
	{
		this.LENGTH = this.LENGTH - programLength;
	}

	public void setLENGTHLITERAL(int length)
	{
		this.LENGTH = length;
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
