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

		//System.out.println(TESTD1);

		//os.schedule(List.of(TESTD1));

		// DSKSCH program schedules multiple writes/reads and interleaves console writes between them
		ProgramBuilder dskSch = new ProgramBuilder();
		dskSch.size(2000);

		for (int i = 1; i <= 100; i++)
		{
			dskSch.loadi(i);
			dskSch.store(1000 + i);
		}

		// Write 1 - 50
		// Device #
		dskSch.loadi(2);
		dskSch.store(800);
		// Platter #
		dskSch.loadi(3);
		dskSch.store(801);
		// Start on platter to write to
		dskSch.loadi(0);
		dskSch.store(802);
		// Length on platter to write
		dskSch.loadi(50);
		dskSch.store(803);
		// Mem in location to write from
		dskSch.loadi(1001);
		dskSch.store(804);
		// Load Acc with mem address
		dskSch.loadi(800);
		// Call DISK WRITE
		dskSch.syscall(3);

		// Interleave Console Write
		dskSch.loadi(777);
		dskSch.syscall(1);

		// Write 51 - 100
		// Device #
		dskSch.loadi(2);
		dskSch.store(805);
		// Platter #
		dskSch.loadi(3);
		dskSch.store(806);
		// Start on platter to write to
		dskSch.loadi(50);
		dskSch.store(807);
		// Length on platter to write
		dskSch.loadi(50);
		dskSch.store(808);
		// Mem in location to write from
		dskSch.loadi(1051);
		dskSch.store(809);
		// Load Acc with mem address
		dskSch.loadi(805);
		// Call DISK WRITE
		dskSch.syscall(3);

		// Interleave Console Write
		dskSch.loadi(777);
		dskSch.syscall(1);

		// READ 3 - 6
		// DEVICE #
		dskSch.loadi(2);
		dskSch.store(810);
		// PLATTER #
		dskSch.loadi(3);
		dskSch.store(811);
		// WHERE ON PLATTER TO START READ FROM
		dskSch.loadi(2);
		dskSch.store(812);
		// LENGTH ON PLATTER TO READ
		dskSch.loadi(4);
		dskSch.store(813);
		// LOCATION IN MEM TO WRITE TO
		dskSch.loadi(1500);
		dskSch.store(814);
		// LOAD ACC WITH MEM LOCATION
		//dskSch.loadi(810);
		// CALL READ
		//dskSch.syscall(2);

		// Interleave Console Write
		dskSch.loadi(777);
		dskSch.syscall(1);

		// READ 1 - 2
		// DEVICE #
		dskSch.loadi(2);
		dskSch.store(815);
		// PLATTER #
		dskSch.loadi(3);
		dskSch.store(816);
		// WHERE ON PLATTER TO START READ FROM
		dskSch.loadi(0);
		dskSch.store(817);
		// LENGTH ON PLATTER TO READ
		dskSch.loadi(2);
		dskSch.store(818);
		// LOCATION IN MEM TO WRITE TO
		dskSch.loadi(1504);
		dskSch.store(819);
		// LOAD ACC WITH MEM LOCATION
		//dskSch.loadi(815);
		// CALL READ
		//dskSch.syscall(2);

		// Interleave Console Write
		dskSch.loadi(777);
		dskSch.syscall(1);

		// READ 8 - 10
		// DEVICE #
		dskSch.loadi(2);
		dskSch.store(820);
		// PLATTER #
		dskSch.loadi(3);
		dskSch.store(821);
		// WHERE ON PLATTER TO START READ FROM
		dskSch.loadi(7);
		dskSch.store(822);
		// LENGTH ON PLATTER TO READ
		dskSch.loadi(3);
		dskSch.store(823);
		// LOCATION IN MEM TO WRITE TO
		dskSch.loadi(1506);
		dskSch.store(824);
		// LOAD ACC WITH MEM LOCATION
		//dskSch.loadi(820);
		// CALL READ
		//dskSch.syscall(2);

		// Interleave Console Write
		dskSch.loadi(777);
		dskSch.syscall(1);

		// READ 12 - 20
		// DEVICE #
		dskSch.loadi(2);
		dskSch.store(825);
		// PLATTER #
		dskSch.loadi(3);
		dskSch.store(826);
		// WHERE ON PLATTER TO START READ FROM
		dskSch.loadi(11);
		dskSch.store(827);
		// LENGTH ON PLATTER TO READ
		dskSch.loadi(9);
		dskSch.store(828);
		// LOCATION IN MEM TO WRITE TO
		dskSch.loadi(1509);
		dskSch.store(829);
		// LOAD ACC WITH MEM LOCATION
		//dskSch.loadi(825);
		// CALL READ
		//dskSch.syscall(2);

		dskSch.loadi(810);
		dskSch.syscall(2);
		dskSch.loadi(815);
		dskSch.syscall(2);
		dskSch.loadi(820);
		dskSch.syscall(2);
		dskSch.loadi(825);
		dskSch.syscall(2);

		for (int i = 1500; i <= 1517; i++)
		{
			dskSch.load(i);
			dskSch.syscall(1);
		}

		dskSch.end();

		Program DSKSCH = dskSch.build();

		//System.out.println(DSKSCH);
		//os.schedule(List.of(DSKSCH));

		// Passed in are the heads, length to write, and operation (1 is CONSOLE WRITE, 2 is READ FROM DISK
		// 3 is WRITE TO DISK). Each is of size (Data size + Code size) of 126. Virtual Location 28 is where the 1st
		// information for the registers is found, thus CONSOLE WRITES will print 28 as that was the last thing loaded
		// into the Accumulator

		// THUS the below satisfies 1) Cause more than one disk write/read to be scheduled at the same time
		//							4) Causes disk to scheduling to re-order disk I/O Requests

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
		//							6) Interleave console writes with disk reads and writes

		LinkedList<Program> list2 = new LinkedList<>();

		// Disk Write
		list2.add(programCreater(0, 1, 3));
		// Console Write
		list2.add(programCreater(0, 1, 1));
		// Disk Read
		list2.add(programCreater(20, 1, 2));
		// Console Write
		list2.add(programCreater(0, 1, 1));
		// Disk Write
		list2.add(programCreater(4, 1, 3));
		// Disk Read
		list2.add(programCreater(2, 1, 2));
		// Console Writes
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));
		list2.add(programCreater(0, 1, 1));

		os.schedule(list2);

		// THUS the below satisfies: 5) Cause Starvation

		ProgramBuilder greedy = new ProgramBuilder();
		greedy.size(100);
		greedy.loadi(777);
		greedy.store(50);

		// Device #
		greedy.loadi(2);
		greedy.store(28);
		// Platter #
		greedy.loadi(1);
		greedy.store(29);
		// Start on platter to write to
		greedy.loadi(0);
		greedy.store(30);
		// Length on platter to write
		greedy.loadi(1);
		greedy.store(31);
		// Mem in location to write from
		greedy.loadi(50);
		greedy.store(32);
		// Load Acc with mem address
		greedy.loadi(28);
		// Call DISK WRITE
		greedy.syscall(3);


		ProgramBuilder starveMe = new ProgramBuilder();
		starveMe.size(100);

		// Device #
		starveMe.loadi(2);
		starveMe.store(33);
		// Platter #
		starveMe.loadi(1);
		starveMe.store(34);
		// Start on platter to write to
		starveMe.loadi(2);
		starveMe.store(35);
		// Length on platter to write
		starveMe.loadi(1);
		starveMe.store(36);
		// Mem in location to write from
		starveMe.loadi(50);
		starveMe.store(37);
		// Load Acc with mem address
		starveMe.loadi(33);
		// Call DISK WRITE
		starveMe.syscall(3);

		starveMe.end();

		Program STARVEME = starveMe.build();
		//System.out.println(STARVEME);

		// Thus the below satisfies: 3) Do a large read/write that requires more than one disk read/write

		ProgramBuilder tooBig = new ProgramBuilder();
		tooBig.size(4000);

		// 200 integers to WRITE and READ
		for(int i = 0; i < 200; i++)
		{
			tooBig.loadi(i);
			tooBig.store(3000 + i);
		}

		// WRITE 1st 100, Entire Platter
		// Device #
		tooBig.loadi(2);
		tooBig.store(2000);
		// Platter #
		tooBig.loadi(1);
		tooBig.store(2001);
		// Start on platter to write to
		tooBig.loadi(0);
		tooBig.store(2002);
		// Length on platter to write
		tooBig.loadi(100);
		tooBig.store(2003);
		// Mem in location to write from
		tooBig.loadi(3000);
		tooBig.store(2004);
		// Load Acc with mem address
		tooBig.loadi(2000);
		// Call DISK WRITE
		tooBig.syscall(3);

		// READ 1st 100, Entire Platter
		// DEVICE #
		tooBig.loadi(2);
		tooBig.store(2005);
		// PLATTER #
		tooBig.loadi(1);
		tooBig.store(2006);
		// WHERE ON PLATTER TO START READ FROM
		tooBig.loadi(0);
		tooBig.store(2007);
		// LENGTH ON PLATTER TO READ
		tooBig.loadi(100);
		tooBig.store(2008);
		// LOCATION IN MEM TO WRITE TO
		tooBig.loadi(4000);
		tooBig.store(2009);
		// LOAD ACC WITH MEM LOCATION
		tooBig.loadi(2005);
		// CALL READ
		tooBig.syscall(2);

		// WRITE 2nd 100, Entire Platter
		// Device #
		tooBig.loadi(2);
		tooBig.store(2010);
		// Platter #
		tooBig.loadi(1);
		tooBig.store(2011);
		// Start on platter to write to
		tooBig.loadi(0);
		tooBig.store(2012);
		// Length on platter to write
		tooBig.loadi(100);
		tooBig.store(2013);
		// Mem in location to write from
		tooBig.loadi(3100);
		tooBig.store(2014);
		// Load Acc with mem address
		tooBig.loadi(2010);
		// Call DISK WRITE
		tooBig.syscall(3);

		// READ 2nd 100, Entire Platter
		// DEVICE #
		tooBig.loadi(2);
		tooBig.store(2015);
		// PLATTER #
		tooBig.loadi(1);
		tooBig.store(2016);
		// WHERE ON PLATTER TO START READ FROM
		tooBig.loadi(0);
		tooBig.store(2017);
		// LENGTH ON PLATTER TO READ
		tooBig.loadi(100);
		tooBig.store(2018);
		// LOCATION IN MEM TO WRITE TO
		tooBig.loadi(4100);
		tooBig.store(2019);
		// LOAD ACC WITH MEM LOCATION
		tooBig.loadi(2015);
		// CALL READ
		tooBig.syscall(2);

		// PRINT WITH CONSOLE WRITE
		for(int i = 4000; i <= 4199; i++)
		{
			tooBig.load(i);
			tooBig.syscall(1);
		}
		tooBig.end();

		Program TOOBIG = tooBig.build();
		//os.schedule(List.of(TOOBIG));

	}

}
