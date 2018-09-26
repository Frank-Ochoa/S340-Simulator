package s340.software.os;

public class FreeSpace
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


}
