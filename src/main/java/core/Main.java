package core;

import org.neuroph.util.TransferFunctionType;

import controllers.Controller;
import controllers.MLP;
import evolver.EvolutionAlgorithm;
import evolver.EvolutionAlgorithm.Type;
import simulations.Simulation;
import simulations.XOR;
import ui.GUI;

public class Main {

	public static boolean runThreads = true;
	
	public static void main(String[] args) {
        Simulation s = new XOR();
        Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.SIN, 4, 4);
        EvolutionAlgorithm ea = new EvolutionAlgorithm(s, c);
        s.setEvolutionAlgorithm(ea);
        ea.setReproductionType(Type.RANDOM);
        ea.setGenerationMultiplier(10);
        ea.setMutationAmt(0.13f);
        ea.setMutationRate(0.15f);
        ea.setFoundersPercent(0.5f);
        ea.setGamesPerElement(5);

        System.out.println("Starting Simulation: " + s);
        new Thread(ea).start();

        GUI.run();
	}
}