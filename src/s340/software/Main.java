package s340.software;

import s340.hardware.Machine;
import s340.hardware.MemoryController;
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

	public static void main(String[] args) throws Exception
	{
		//	setup the hardware, the operating system, and power up
		//	do not remove this
		Machine machine = new Machine();
		OperatingSystem os = new OperatingSystem(machine);
		machine.powerUp(os);

		// create a program

		// Sum of first 100 integers
		ProgramBuilder b1 = new ProgramBuilder();
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
		b4.end();

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
			px.storex(28 - p);
			px.add(38);
			px.store(38);
			px.incx();
			px.jmp(4);
			px.load(38);
			px.output();
			px.end();

			Program px1 = px.build();
			//System.out.println(px1);

			toBeRan.add(px1);
		}

		os.schedule(toBeRan);*/

		ProgramBuilder x1 = new ProgramBuilder();
		x1.loadi(8);
		x1.output();
		x1.end();

		ProgramBuilder x2 =  new ProgramBuilder();
		x2.loadi(5);
		x2.output();
		x2.end();

		ProgramBuilder x3 = new ProgramBuilder();
		x3.loadi(2);
		x3.loadi(3);
		x3.loadi(4);
		x3.loadi(5);
		x3.loadi(4);
		x3.syscall(0);
		x3.output();
		x3.end();

		ProgramBuilder x4 = new ProgramBuilder();
		x4.loadi(15);
		x4.loadi(15);
		x4.loadi(15);
		x4.loadi(16);
		x4.output();
		x4.end();

		ProgramBuilder x5 = new ProgramBuilder();
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);
		x5.loadi(15);



		Program X1 = x1.build();
		Program X2 = x2.build();
		Program X3 = x3.build();
		Program X4 = x4.build();

		// X3, X1, X2, X4 for merging testing

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

		// Size of 7, limit 8
		ProgramBuilder mem2 = new ProgramBuilder();
		mem2.loadi(10);
		mem2.loadi(10);
		mem2.output();
		mem2.end();

		// Size of 100
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





		os.schedule(List.of(MEM2, MEM3, MEM1));

	}

}
