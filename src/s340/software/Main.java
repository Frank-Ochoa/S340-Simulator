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
		// 170 is the sum
		b3.load(i2);
		b3.mul(i2);
		// Add the square of 160
		b3.add(sum2);
		b3.store(sum2);
		b3.incx();
		b3.jmp(306);
		b3.load(sum2);
		b3.output();
		b3.end();

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



		Program p1 = b1.build();
		Program p2 = b2.build();
		Program p3 = b3.build();
		Program p4 = b4.build();
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		// schedule the program
		os.schedule(List.of(p1, p2, p3, p4));
	}
}
