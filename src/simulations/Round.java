package simulations;

import controllers.Controller;
import ui.Builder.Constraint;
import ui.Builder.TabMenu;
import ui.Builder.MenuItems;
import ui.Builder.MenuItems.EntryType;
import util.*;

public class Round extends Simulation {
    int trialCount;
    MenuItems inputF = new MenuItems();

    @Override
    public double[] simulate(Controller[] c) {
        //		float[] d = {0.7f,0.1f,0.22f,0.43f,0.4f,0.51f,0.62f,0.99f};
        //System.out.println(trialCount);
        //TODO: Remove Trial Count for this sim
        double[] errors = new double[] {0};
        for (int i = 0; i < trialCount; i++) {
            float rand = Rand.r.nextFloat();
            double out = c[0].calculate(rand)[0];
            //System.out.println(out);
            double error = 0;
            if (rand >= 0.5) error = 1 - out;
            else error = out;

            errors[0] += -error * 10;
        }
        return errors;
    }

    @Override
    public int getNumInputs() {
        return 1;
    }

    @Override
    public int getNumOutputs() {
        return 1;
    }

    @Override
    public int getControlPerSim() {
        return 1;
    }

    @Override
    public String toString() {
        return "Rounding Simulation";
    }

    @Override
    public Simulation copy() {
        return new Round();
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public boolean start() {
        trialCount = numTrials.getValue();
        return true;
    }

    IntegerHolder numTrials = new IntegerHolder(5);

    @Override
    public void menuInit(MenuItems inputF) {
        inputF.add("Trial Count", EntryType.SLIDER, numTrials, new Constraint(1, 20), true);
    }
}
