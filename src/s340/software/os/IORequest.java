package s340.software.os;

public class IORequest
{
	// Needs an operation, either read or write
	private int operations;
	private ProcessControlBlock sourceProcess;

	public IORequest(int operations, ProcessControlBlock sourceProcess)
	{
		this.operations = operations;
		this.sourceProcess = sourceProcess;
	}

	public int getOperations()
	{
		return operations;
	}

	public ProcessControlBlock getSourceProcess()
	{
		return sourceProcess;
	}

	@Override public String toString()
	{
		return "IORequest{" + "operations=" + operations + ", sourceProcess=" + sourceProcess + '}';
	}
}
