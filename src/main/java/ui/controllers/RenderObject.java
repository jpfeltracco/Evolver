package ui.controllers;

import java.util.Arrays;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import simulations.Renderable;
import simulations.Simulation;

public class RenderObject implements Runnable{

	private final EvolutionAlgorithm ea;
	private final VTab vTab;
	private Simulation renderSim;
	private final EATab eaTab;
	private boolean running = true;
	
	public RenderObject(EvolutionAlgorithm ea, VTab vTab, Simulation renderSim, EATab eaTab){
		this.ea = ea;
		this.vTab = vTab;
		this.renderSim = renderSim;
		this.eaTab = eaTab;
	}
	
	@Override
	public void run() {
		while(!ea.initSucess){
			System.out.println("Waiting for EA");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!GUI.running || !running)
				return;
		}
		vTab.stop();
		while(ea.getActive()){
			System.out.println("Waiting for EA");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!GUI.running || !running)
				return;
		}
		
		ElementHolder elementHolder = ea.getExportedElements();
		Arrays.sort(elementHolder.getElements());
		
		Controller[] controllers = new Controller[vTab.simulation.getControlPerSim()];
		for (int i = 0; i < vTab.simulation.getControlPerSim(); i++) {
			controllers[i] = vTab.controller.clone();
			controllers[i].setConfig(elementHolder.getElements()[elementHolder.getElements().length - 1 - i].clone());
		}
		
		renderSim = vTab.simulation.clone();
		// TODO Make controllers accessible to Pong directly so we don't have to pass into render
		renderSim.setControllers(controllers);
		((Renderable) renderSim).render(controllers, eaTab);
	}
	

}
