package s340.software;

import java.util.List;

import s340.hardware.Machine;
import s340.software.os.Program;
import s340.software.os.OperatingSystem;
import s340.software.os.ProgramBuilder;

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
        /*b1.start(50);

        int SUM = 120;
        int I = 90;

        b1.loadi(0);
        b1.store(SUM);
        b1.loadi(0);
        b1.store(I);
        int back = b1.load(I);
        b1.subi(10);
        b1.jpos(28);
        b1.load(SUM);
        b1.add(I);
        b1.store(SUM);
        b1.load(I);
        b1.addi(1);
        b1.store(I);
        b1.jmp(back);
        b1.load(SUM);
        b1.output();
        b1.end();*/

      // Question 1 Stuff

      /* int sum = 0;
       for(int i= 0; i <= 100; i++)
       {
           sum += i;
       }

       System.out.println(sum);

       int sum2 = 0;
       int i2 = 0;
       while(i2 <= 100)
       {
           sum2 += i2;
           i2++;
       }

       System.out.println(sum2);

       b1.loadi(0);
       b1.store(200);
       b1.tax();
       b1.txa();
       b1.store(500);
       b1.subi(101);
       b1.jpos(26);
       b1.jzero(26);
       b1.load(200);
       b1.add(500);
       b1.store(200);
       b1.incx();
       b1.jmp(6);
       b1.load(200);
       b1.output();
       b1.end();*/

      // Question 2 Stuff

      /* for(int i = 1; i <= 5; i++)
       {
           for(int j = 1; j <= 5; j++)
           {
               System.out.println(i * j);
           }
       }*/


      int j = 100;
      int k = 200;

      b1.loadi(1);
      b1.store(j);
      b1.subi(6);
      b1.jpos(42);
      b1.jzero(42);
      b1.loadi(1);
      b1.store(k);
      b1.tax();
      b1.txa();
      b1.store(k);
      b1.subi(6);
      b1.jpos(36);
      b1.jzero(36);
      b1.load(j);
      b1.mul(k);
      b1.store(900);
      b1.incx();
      b1.jmp(16);
      b1.load(j);
      b1.addi(1);
      b1.jmp(2);

      b1.load(900);
      b1.output();
      b1.end();


		Program p1 = b1.build();
		System.out.println(p1);

		// schedule the program
		os.schedule(List.of(p1));
	}
}
