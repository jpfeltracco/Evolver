package com.jeremyfeltracco.core.evolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
	private final float mutationAmt;
	private final float mutationRate;
	private final Simulation simType;
	private final int controlPerSim;
	private int availableControllers;
	private Element[] elements;
	private final int numThreads;
	private Controller[] controllers;
	private Controller controllerType;
	
	public int genNum = 0;

	public EvolutionAlgorithm(Type t, int mult, float mutationAmt, float mutationRate, Simulation sim, Controller controller){
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

		for(int i = 0; i < availableControllers; i++)
			controllers[i] = controllerType.clone();
		
		Element.numElements = 0;
		for (int i = 0; i < elements.length; i++)
			elements[i] = controllerType.generateRandomConfig();
		
	}

	@Override
	public void run() {
		while (Main.runThreads) {
			// Setup simulations
			
			/*for(int i = 0; i < elements.length; i++)
				System.out.print(elements[i].id + ", ");
			System.out.println();*/
			
			int gamesPerElement = 3;
			ArrayList<Element> elementHolder = new ArrayList<Element>();
			for(int i = 0; i < elements.length; i++)
				for(int j = 0; j < gamesPerElement; j++)
					elementHolder.add(elements[i]);
			
			Collections.shuffle(elementHolder);

			
			Element[] appliedElements = new Element[gamesPerElement * elements.length];
			for(int i = 0; i < appliedElements.length; i++)
				appliedElements[i] = elementHolder.get(i);

			
			/*for(int i = 0; i < appliedElements.length; i++)
				System.out.print(appliedElements[i].id + ", ");
			System.out.println("\nLEN: " + appliedElements.length);*/
			
			Simulation[] sims = new Simulation[numThreads];
			for(int i = 0; i < numThreads; i++){
				sims[i] = simType.clone();
				sims[i].setEvolutionAlgorithm(this);
				
				Controller[] appliedControllers = new Controller[controlPerSim];
				for(int j = 0; j < controlPerSim; j++)
					appliedControllers[j] = controllers[i*controlPerSim + j];
				
				sims[i].setControllers(appliedControllers);
				
				
				int val = appliedElements.length / numThreads;
				
				Element[] el = Arrays.copyOfRange(appliedElements, i*val, (i+1)*val);
				
				sims[i].setElements(el);
				
			}
			
			
			Thread[] threads = new Thread[numThreads];
			for(int i = 0; i < numThreads; i++){
				threads[i] = new Thread(sims[i]);
				threads[i].start();
			}
			
			try{
				for(Thread t : threads)
					t.join();
			}catch(InterruptedException e){}
				
			
			
			Arrays.sort(elements);
			/*for(Element e : elements)
				System.out.print(e.id + "\t");
			System.out.println();
			for(Element e : elements)
				System.out.print(e.getFitness() + "\t");
			System.out.println();*/
			

			if(Math.abs(elements[elements.length-1].getFitness()) < 0.1)
				System.out.println("Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
			
			//if(genNum%500==0){
			//}
			
			float curve = 2.0f;
			float x;
			Element.numElements = 0;
			Element[] nextGen = new Element[elements.length];
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("1Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
			//Elitism
			nextGen[0] = elements[elements.length-1];
			nextGen[0].setFitness(0);
			
			
			//System.out.println("Gen: " + genNum);
			
		
			//-------
			for(int i = 1; i < elements.length; i++){
				float a = (float)(1 / Math.log(curve + 1) * elements.length);
				x = MathUtils.random();
				int e1Ind = (int)(Math.log(curve * x + 1) * a);
				Element e1 = elements[e1Ind];
				//System.out.print("e1: " + (int)(Math.log(curve * x + 1) * a));
				int e2Ind;
				do{
					x = MathUtils.random();
					e2Ind = (int)(Math.log(curve * x + 1) * a);
				}while(e1Ind == e2Ind);
				Element e2 = elements[e2Ind];
				//System.out.println(", e2: " + (int)(Math.log(curve * x + 1) * a));
				nextGen[i] = reproduce(e1, e2);
			}
			genNum++;
			elements = nextGen;
			/*ArrayList<Element> elementHolder2 = new ArrayList<Element>();
			for(int i = 0; i < elements.length; i++)
				for(int j = 0; j < gamesPerElement; j++)
					elementHolder2.add(elements[i]);
			
			Collections.shuffle(elementHolder2);
			
			for(int i = 0; i < elements.length; i++)
				elements[i] = elementHolder2.get(i);*/
			
			//System.out.println("2Element: " + elements[0].id + "\t Fitness: " + elements[0].getFitness());
			//for(Element e : elements)
				//System.out.print(e.id + "\t");
			//System.out.println();
			
			/*for(Element e : elements)
				System.out.print(e.id + "\t");
			System.out.println();
			for(Element e : elements)
				System.out.print(e.getFitness() + "\t");
			System.out.println();*/
			
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



}
