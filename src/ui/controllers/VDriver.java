package ui.controllers;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import goals.Goal;
import simulations.Simulation;
import ui.Builder.MenuItems;
import ui.graph.DataBridge;

public class VDriver {

    public static int vDriverCount = 0;

    final VTab vTab;
    final Goal goal;
    DataBridge dataBridge = new DataBridge(this);
    ElementHolder elements;

    byte status = 0;

    public VDriver(
            Simulation simulation,
            Controller controller,
            MenuItems eaMenuItems,
            ElementHolder elements,
            Goal goal) {
        addNumVDrivers(1);
        System.out.println("Starting VDriver...");
        this.elements = elements;
        this.goal = goal;
        System.out.println("Creating VTab...");

        vTab =
                new VTab(
                        simulation,
                        controller,
                        new EvolutionAlgorithm(),
                        eaMenuItems,
                        elements,
                        dataBridge);
        goal.activate(elements);
        vTab.activate();
        status = 1;
    }

    public void check(ElementHolder elements) {
        status = 2;
        if (goal.check(elements)) {
            status = 4;
            vTab.stop();
            System.out.println("VDriver Complete. Shutting down VTab...");
            elements = vTab.getElements();
            System.out.println("Goal details:\n\t" + goal.exitReason());
            System.out.println("Memo: " + goal.message());
            addNumVDrivers(-1);
            //TODO: Handle the result!
        }
    }

    /* 0: Initializing
     * 1: Running
     * 2: Checking Goal
     * 3: Stopped
     * 4: Done
     * 5: Error
     */
    public byte getStatus() {
        return status;
    }

    public ElementHolder getElementOutput() {
        return elements;
    }

    public byte[][][] getGraphData() {
        return dataBridge.streamData();
    }

    public static synchronized int getNumVDrivers() {
        return vDriverCount;
    }

    public static synchronized void addNumVDrivers(int val) {
        vDriverCount += val;
    }
}
