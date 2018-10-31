package s340.software.os;

/*
 * System call numbers for the operating system.
 */
public interface SystemCall
{
	public final static int NUM_SYSTEM_CALLS = 4;
	public final static int REQUEST_MORE_MEMORY = 0;
	public static final int WRITE_TO_CONSOLE = 1;
	public static final int READ_TO_DISK = 2;
	public static final int WRITE_TO_DISK = 3;


}
