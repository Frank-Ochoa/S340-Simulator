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
        b1.start(50);

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
        b1.end();

        Program p1 = b1.build();
        System.out.println(p1);

        // schedule the program
        os.schedule(List.of(p1));
    }
}
