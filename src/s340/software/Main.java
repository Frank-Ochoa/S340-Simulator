package s340.software;

import s340.hardware.Machine;
import s340.software.os.OperatingSystem;
import s340.software.os.Program;
import s340.software.os.ProgramBuilder;

import java.util.List;

/**
 *
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
		ProgramBuilder b1 = new ProgramBuilder();

		b1.start(50);
		b1.loadi(0);
		b1.store(140);
		b1.tax();
		b1.txa();
		b1.store(160);
		b1.subi(101);
		b1.jpos(76);
		b1.jzero(76);
		b1.load(140);
		b1.add(160);
		b1.store(140);
		b1.incx();
		b1.jmp(56);
		b1.load(140);
		b1.output();
		b1.end();

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

		// Question 6

	  /*int a = 121;

	  b1.loadi(4);
	  b1.store(a);
	  b1.loadi(0);
	  b1.tax();
	  b1.txa();
	  b1.subi(20);
	  b1.jpos(26);
	  b1.jzero(26);
	  b1.loadx(a);
	  b1.addi(1);
	  b1.incx();
	  b1.storex(a);
	  b1.jmp(8);
	  b1.loadi(0);
	  b1.tax();
	  b1.txa();
	  b1.subi(20);
	  b1.jpos(46);
	  b1.jzero(46);
	  b1.loadx(a);
	  b1.incx();
	  b1.output();
	  b1.jmp(30);
	  b1.end();*/

		ProgramBuilder b3 = new ProgramBuilder();
		b3.start(800);
		b3.loadi(10);
		b3.output();
		b3.end();

		Program p1 = b1.build();
		Program p2 = b2.build();
		Program p3 = b3.build();
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		// schedule the program
		os.schedule(List.of(p1, p2));
	}
}
