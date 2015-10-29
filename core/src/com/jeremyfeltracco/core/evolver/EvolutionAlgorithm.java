package com.jeremyfeltracco.core.evolver;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.Main;
import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.simulations.Simulation;

public class EvolutionAlgorithm implements Runnable {

	public enum Type {
		HALF, RANDOM
	}

	private final Type reproductionType;
	private final int numPerGen;
	private final int mutationAmt;
	private final float mutationRate;
	private final Simulation simType;
	private final int controlPerSim;
	private int availableControllers;
	private Element[] elements;
	private final int numThreads;
	private Controller[] controllers;
	private Controller controllerType;
	
	public int genNum = 0;

	public EvolutionAlgorithm(Type t, int mult, int mutationAmt, float mutationRate, Simulation sim, Controller controller){
		this.reproductionType = t;
		this.mutationAmt = mutationAmt;
		this.mutationRate = mutationRate;
		this.simType = sim;
		this.controllerType = controller;
		
		controlPerSim = simType.getControlPerSim();
		availableControllers = controllerType.getAvailableControllers();
		
		
		numThreads = availableControllers / controlPerSim; 
		numPerGen = mult * (numThreads * controlPerSim);
		
		System.out.println("numThreads: " + numThreads);
		System.out.println("controlPerSim: " + controlPerSim);
		System.out.println("NumPerGen: " + numPerGen);
		
		elements = new Element[numPerGen];
		
		controllers = new Controller[availableControllers];
		controllers[0] = null;
		for(int i = 0; i < availableControllers; i++){
			controllers[i] = controllerType.clone();

		}
		
		Element.numElements = 0;
		for (int i = 0; i < elements.length; i++)
			elements[i] = controllerType.generateRandomConfig();
		
	}

	@Override
	public void run() {
		
		
		while (Main.runThreads) {
			// Setup simulations
			
			for(int i = 0; i < elements.length; i++)
				System.out.print(elements[i].id + ", ");
			System.out.println();
			
			int gamesPerElement = 5 - 1;
			ArrayList<Element> elementHolder = new ArrayList<Element>(Arrays.asList(elements));
			Element[] appliedElements = new Element[gamesPerElement * elements.length];
			for(int i = 0; i < appliedElements.length; i++){
				appliedElements[i] = elementHolder.get((int)(MathUtils.random() * elementHolder.size()));
				
				if(appliedElements[i].incrementGame() >= gamesPerElement)
					elementHolder.remove(appliedElements[i]);
				
			}
			
			Simulation[] sims = new Simulation[numThreads];
			for(int i = 0; i < numThreads; i++){
				sims[i] = simType.clone();
				sims[i].setEvolutionAlgorithm(this);
				
				Controller[] appliedControllers = new Controller[controlPerSim];
				for(int j = 0; j < controlPerSim; j++)
					appliedControllers[j] = controllers[i*controlPerSim + j];
				
				sims[i].setControllers(appliedControllers);
				
				
				int val = appliedElements.length / numThreads;
				for(int j = 0; j < appliedElements.length; j++)
					System.out.print(appliedElements[j].id + ", ");
				System.out.println();
				
				Element[] el = Arrays.copyOfRange(appliedElements, i*val, (i+1)*val);
				
				for(int j = 0; j < appliedElements.length; j++)
					System.out.print(appliedElements[j].id + ", ");
				System.out.println();
//				System.out.println("-----------------------------------------------");
//				for(Element e : el)
//					System.out.print(e.id + " ");
//				System.out.println();
				
				sims[i].setElements(el);
				
				Simulation.simsRunning++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			//ACTIVATE THREAD (finish this portion
				//For Loop to do so (IE. run thread.start()
			
			
			
//			for(int i = 0; i < elements.length; i++)
//				if(elements[i].id == 1)
//					System.out.println("Before: " + elements[i].getFitness());
			
			for(Simulation s : sims)
				new Thread(s).start();
			
			waitForThreads();
			
			
			
			Arrays.sort(elements);
			
			if(elements[elements.length-1].getFitness() > -0.1)
				System.out.println("Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
			
			if(genNum%500==0)
				System.out.println("Gen: " + genNum);
			
			float curve = 6.0f;
			float x;
			Element[] nextGen = new Element[elements.length];
			Element.numElements = 0;
			//Elitism
			//nextGen[0] = elements[elements.length-1];
			//nextGen[1] = elements[elements.length-2];
			//-------
			for(int i = 0; i < elements.length; i++){
				float a = (float)(1 / Math.log(curve + 1) * elements.length);
				x = MathUtils.random();
				Element e1 = elements[(int)(Math.log(curve * x + 1) * a)];
				x = MathUtils.random();
				Element e2 = elements[(int)(Math.log(curve * x + 1) * a)];
				
				nextGen[i] = reproduce(e1, e2);
			}
			
			genNum++;
			elements = nextGen;
		}
	}
	
	
	private Element reproduce(Element e1, Element e2){
		Element tmp = new Element();
		double[] weights = new double[e1.config.length];
		
		int cut = e1.config.length/2;
		switch(reproductionType){
		case RANDOM:
			cut = (int)(MathUtils.random() * e1.config.length);
		case HALF:
			for(int i = 0; i < cut; i++)
				weights[i] = e1.config[i];
			for(int i = cut; i < e1.config.length; i++)
				weights[i] = e2.config[i];
			break;
		}
		tmp.config = weights;
		
		if (MathUtils.random() < mutationRate)
			controllerType.mutateElement(tmp, mutationAmt);
		
		return tmp;
	}

	private synchronized void waitForThreads(){
		while(Simulation.simsRunning != 0){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
