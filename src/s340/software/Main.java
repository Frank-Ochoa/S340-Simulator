package s340.software;

import s340.hardware.Machine;
import s340.software.os.OperatingSystem;
import s340.software.os.Program;
import s340.software.os.ProgramBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Project 1 by : Frank Ochoa, Aidan Chisum, Alec Bennett
 */
public class Main
{
	public static Program programCreater(int head, int length, int operation)
	{
		ProgramBuilder write = new ProgramBuilder();
		write.size(100);

		// Device #
		write.loadi(2);
		write.store(30);
		// Platter #
		write.loadi(1);
		write.store(31);
		// Start on platter to write to
		write.loadi(head);
		write.store(32);
		// Length on platter to write
		write.loadi(length);
		write.store(33);
		// Mem in location to write from
		write.loadi(100);
		write.store(34);
		// Load Acc with mem address
		write.loadi(30);
		// Call DISK WRITE
		write.syscall(operation);

		write.end();

		return write.build();
	}


	public static void main(String[] args) throws Exception
	{
		//	setup the hardware, the operating system, and power up
		//	do not remove this
		Machine machine = new Machine();
		OperatingSystem os = new OperatingSystem(machine);
		machine.powerUp(os);

		// create a program

		// Sum of first 100 integers
		/*ProgramBuilder b1 = new ProgramBuilder();
		int sum = 140;
		int i = 160;

		b1.start(50);
		b1.loadi(0);
		b1.store(sum);
		b1.tax();
		b1.txa();
		b1.store(i);
		b1.subi(101);
		b1.jpos(76);
		b1.jzero(76);
		b1.load(sum);
		b1.add(i);
		b1.store(sum);
		b1.incx();
		b1.jmp(56);
		b1.load(sum);
		b1.output();
		b1.end();

		// Compute and print j * k for each j and k between 1 and 5
		ProgramBuilder b2 = new ProgramBuilder();
		int j = 500;
		int k = 600;

		b2.start(200);
		b2.loadi(1);
		b2.store(j);
		b2.subi(6);
		b2.jpos(242);
		b2.jzero(242);
		b2.loadi(1);
		b2.store(k);
		b2.tax();
		b2.txa();
		b2.store(k);
		b2.subi(6);
		b2.jpos(236);
		b2.jzero(236);
		b2.load(j);
		b2.mul(k);
		b2.output();
		b2.incx();
		b2.jmp(216);
		b2.load(j);
		b2.addi(1);
		b2.jmp(202);
		b2.end();

		// Sum of the squares of the first 100 integers
		ProgramBuilder b3 = new ProgramBuilder();
		int sum2 = 170;
		int i2 = 180;

		b3.start(300);
		b3.loadi(0);
		b3.store(sum2);
		b3.tax();
		b3.txa();
		b3.store(i2);
		b3.subi(101);
		b3.jpos(328);
		b3.jzero(328);
		b3.load(i2);
		b3.mul(i2);
		b3.add(sum2);
		b3.store(sum2);
		b3.incx();
		b3.jmp(306);
		b3.load(sum2);
		b3.output();
		b3.end();

		// Compute and print j * k for each j and k between 31 and 35
		ProgramBuilder b4 = new ProgramBuilder();
		int j2 = 550;
		int k2 = 650;

		b4.start(800);
		b4.loadi(31);
		b4.store(j2);
		b4.subi(36);
		b4.jpos(842);
		b4.jzero(842);
		b4.loadi(31);
		b4.store(k2);
		b4.tax();
		b4.txa();
		b4.store(k2);
		b4.subi(36);
		b4.jpos(836);
		b4.jzero(836);
		b4.load(j2);
		b4.mul(k2);
		b4.output();
		b4.incx();
		b4.jmp(816);
		b4.load(j2);
		b4.addi(1);
		b4.jmp(802);
		b4.end();*/

		/*Program p1 = b1.build();
		Program p2 = b2.build();
		Program p3 = b3.build();
		Program p4 = b4.build();
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		// schedule the program*/
		//os.schedule(List.of(p1, p2, p3, p4));

		// Output numbers are explained as such: A program is selected and ran, after 4 lines of code are ran of said
		// program, the trap handler is entered and another program is selected to be ran. So on and so forth.

		// In this specific case, the output can be explained as such: p1 is selected and its output has not been ran yet,
		// trap is entered, p2 is selected and its output is ran, trap is entered, p3 is selected and is output has
		// not been ran yet, trap is entered, p4 is selected to be ran and its output is ran. On and on, until finally
		// p2 and p4 instructions have been completed, then p1 instructions are completed and its output is ran,
		// and finally p4 instructions have been completed and its output ran. Afterwards the wait process is continually ran
		// as no more programs with READY status exist in the process table.

		/* Project 2 */
		/*List<Program> toBeRan = new LinkedList<>();

		for(int p = 1; p <= 5; p++)
		{
			ProgramBuilder px = new ProgramBuilder();
			px.size(10);
			px.loadi(p);
			px.tax();
			px.txa();
			px.subi(p + 10);
			px.jzero(22);
			px.loadi(p);
			px.storex(27 - p);
			px.add(37);
			px.store(37);
			px.incx();
			px.jmp(4);
			px.load(37);
			px.output();
			px.end();

			Program px1 = px.build();
			//System.out.println(px1);

			toBeRan.add(px1);
		}

		os.schedule(toBeRan);

		ProgramBuilder mem1 = new ProgramBuilder();
		// Size of 50
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(20);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(1);
		mem1.loadi(20);
		mem1.output();
		mem1.end();

		// Size of 8
		ProgramBuilder mem2 = new ProgramBuilder();
		mem2.loadi(10);
		mem2.loadi(10);
		mem2.output();
		mem2.end();

		// I'm an idiot for not using a for loop, although copy paste is pretty neat too
		ProgramBuilder mem3 = new ProgramBuilder();
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.syscall(0);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(30);
		mem3.loadi(70);
		mem3.output();
		mem3.end();


		Program MEM1 = mem1.build();
		Program MEM2 = mem2.build();
		Program MEM3 = mem3.build();

		// Mem1-3 for Expanding in Place, Merging, and Mem Compaction (Left and Right)
		//os.schedule(List.of(MEM2, MEM3, MEM1));


		// Size of 10
		ProgramBuilder move1 = new ProgramBuilder();
		move1.loadi(10);
		move1.loadi(10);
		move1.loadi(69);
		move1.syscall(0);
		move1.loadi(777);
		move1.output();
		move1.end();

		ProgramBuilder move2 = new ProgramBuilder();
		for(int x = 0; x < 50; x++)
		{
			move2.loadi(50);
		}
		move2.output();
		move2.end();

		Program MOVE1 = move1.build();
		Program MOVE2 = move2.build();

		// MOVE1 & MOVE2 for testing moving a program
		//os.schedule(List.of(MOVE1, MOVE2));

		ProgramBuilder longProgram = new ProgramBuilder();
		for(int x = 1; x < 500; x++)
		{
			longProgram.loadi(1);
		}
		longProgram.output();
		longProgram.end();

		ProgramBuilder shortProgram = new ProgramBuilder();
		shortProgram.loadi(1001);
		shortProgram.output();
		shortProgram.end();

		ProgramBuilder sbrkCaller = new ProgramBuilder();
		for(int x = 1; x < 20; x++)
		{
			sbrkCaller.loadi(40);
			sbrkCaller.syscall(0);
			sbrkCaller.loadi(777);
			sbrkCaller.loadi(777);
			sbrkCaller.loadi(777);
			sbrkCaller.loadi(777);
		}
		sbrkCaller.output();
		sbrkCaller.end();

		Program LONGPROGRAM = longProgram.build();
		Program SHORTPROGRAM = shortProgram.build();
		Program SBRKCALLER = sbrkCaller.build();

		// LONGPROGRAM, SHORTPROGRAM, & SBRKCALLER Does Moving, Expanding, Merging, and Memory Compaction
		// Also is what the SampleProgram_Comments&Pictures included PDF is depicting
		//os.schedule(List.of(SBRKCALLER, LONGPROGRAM, SHORTPROGRAM));*/

		/* GROUP PROJECT 3 START */

		// P1-P3 writes i into locations 40-44 and then prints locations 40-44
		ProgramBuilder p1 = new ProgramBuilder();
		p1.size(12);
		p1.loadi(1);
		p1.store(40);
		p1.store(41);
		p1.store(42);
		p1.store(43);
		p1.store(44);
		p1.load(40);
		p1.syscall(1);
		p1.load(41);
		p1.syscall(1);
		p1.load(42);
		p1.syscall(1);
		p1.load(43);
		p1.syscall(1);
		p1.load(44);
		p1.syscall(1);
		p1.end();

		ProgramBuilder p2 = new ProgramBuilder();
		p2.size(12);
		p2.loadi(2);
		p2.store(40);
		p2.store(41);
		p2.store(42);
		p2.store(43);
		p2.store(44);
		p2.load(40);
		p2.syscall(1);
		p2.load(41);
		p2.syscall(1);
		p2.load(42);
		p2.syscall(1);
		p2.load(43);
		p2.syscall(1);
		p2.load(44);
		p2.syscall(1);
		p2.end();

		ProgramBuilder p3 = new ProgramBuilder();
		p3.size(12);
		p3.loadi(3);
		p3.store(40);
		p3.store(41);
		p3.store(42);
		p3.store(43);
		p3.store(44);
		p3.load(40);
		p3.syscall(1);
		p3.load(41);
		p3.syscall(1);
		p3.load(42);
		p3.syscall(1);
		p3.load(43);
		p3.syscall(1);
		p3.load(44);
		p3.syscall(1);
		p3.end();

		Program P1 = p1.build();
		Program P2 = p2.build();
		Program P3 = p3.build();

		//System.out.println(P1);

		//os.schedule(List.of(P1, P2, P3));

		// TESTD1 does the storing of 1-20, writing it, reading it, and then printing it
		ProgramBuilder testD1 = new ProgramBuilder();
		testD1.size(2000);
		for (int i = 1; i <= 20; i++)
		{
			testD1.loadi(i);
			testD1.store(245 + i);
		}
		// Write
		// Device #
		testD1.loadi(2);
		testD1.store(400);
		// Platter #
		testD1.loadi(3);
		testD1.store(401);
		// Start on platter to write to
		testD1.loadi(31);
		testD1.store(402);
		// Length on platter to write
		testD1.loadi(20);
		testD1.store(403);
		// Mem in location to write from
		testD1.loadi(246);
		testD1.store(404);
		// Load Acc with mem address
		testD1.loadi(400);
		// Call DISK WRITE
		testD1.syscall(3);
		// READ
		// DEVICE #
		testD1.loadi(2);
		testD1.store(405);
		// PLATTER #
		testD1.loadi(3);
		testD1.store(406);
		// WHERE ON PLATTER TO START READ FROM
		testD1.loadi(31);
		testD1.store(407);
		// LENGTH ON PLATTER TO READ
		testD1.loadi(20);
		testD1.store(408);
		// LOCATION IN MEM TO WRITE TO
		testD1.loadi(301);
		testD1.store(409);
		// LOAD ACC WITH MEM LOCATION
		testD1.loadi(405);
		// CALL READ
		testD1.syscall(2);
		// OUT WITH WRITE CONSOLE
		for (int b = 301; b <= 320; b++)
		{
			testD1.load(b);
			testD1.syscall(1);
		}
		testD1.end();

		Program TESTD1 = testD1.build();

		//os.schedule(List.of(TESTD1));

		// Passed in are the heads, length to write, and operation (1 is CONSOLE WRITE, 2 is READ FROM DISK
		// 3 is WRITE TO DISK). Each is of size (Data size + Code size) of 126. Virtual Location 30 is where the 1st
		// information for the registers is found, thus CONSOLE WRITES will print 28 as that was the last thing loaded
		// into the Accumulator

		// THUS the below satisfies 1) Cause more than one disk write/read to be scheduled at the same time
		//							3) Causes disk to scheduling to re-order disk I/O Requests

		LinkedList<Program> list = new LinkedList<>();

		// Disk Write
		list.add(programCreater(0, 1, 3));
		// Disk Write
		list.add(programCreater(20, 1, 3));
		// Disk Write
		list.add(programCreater(4, 1, 3));
		// Disk Write
		list.add(programCreater(2, 1, 3));


		//os.schedule(list);

		// THUS the below satisfies 2) Cause more than one console write to occur at the same time
		//							5) Interleave console writes with disk reads and writes

		LinkedList<Program> list2 = new LinkedList<>();

		// Disk Write
		list2.add(programCreater(0, 1, 3));
		// Console Write
		list2.add(programCreater(0, 1, 1));
		// Disk Read
		list2.add(programCreater(20, 1, 2));
		// Console Writes
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));

		//os.schedule(list2);

		// THUS the below satisfies: 4) Cause Starvation

		LinkedList<Program> list3 = new LinkedList<>();
		list3.add(programCreater(0, 1, 3));
		list3.add(programCreater(3, 1, 3));
		list3.add(programCreater(5, 1, 3));
		list3.add(programCreater(7, 1, 3));
		list3.add(programCreater(90, 1, 3));
		list3.add(programCreater(9, 1, 3));
		list3.add(programCreater(11, 1, 3));
		list3.add(programCreater(13, 1, 3));
		list3.add(programCreater(15, 1, 3));

		os.schedule(list3);

	}

}
