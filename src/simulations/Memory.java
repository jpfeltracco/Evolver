package simulations;

import controllers.Controller;
import ui.Builder.MenuItems;
import java.util.Random;

public class Memory extends Simulation {
    static int x = 0;
    Random r = new Random();

    @Override
    public double[] simulate(Controller[] c) {
        float testerVal = r.nextFloat(); // some new original value
        float mem = r.nextFloat(); // some random old value

        // First input is next val, second is mem val
        double[] outputs = c[0].calculate(testerVal, mem); // only use this to get a new mem

        float rand = r.nextFloat(); // New val to put in
        double[] newOutputs = c[0].calculate(rand, outputs[1]); // put in new val and mem from prev

        double error = Math.abs(testerVal - newOutputs[0]);

        return new double[] {-error};
    }

    @Override
    public int getNumInputs() {
        return 2;
    }

    @Override
    public int getNumOutputs() {
        return 2;
    }

    @Override
    public int getControlPerSim() {
        return 1;
    }

    @Override
    public Simulation copy() {
        return new Memory();
    }

    @Override
    public String toString() {
        return "Memory thing.";
    }

    @Override
    public void menuInit(MenuItems inputF) {}

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }
}
