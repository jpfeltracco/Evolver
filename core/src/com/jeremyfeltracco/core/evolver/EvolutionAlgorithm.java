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

	private Type reproductionType = Type.HALF;
	private float mutationAmt = 0.13f;
	private float mutationRate = 0.15f;
	private float foundersPercent = 0.5f;
	private int mult = 10;
	private int gamesPerElement = 5;
	
	private final int numPerGen;
	private final Simulation simType;
	private final int controlPerSim;
	private int availableControllers;
	private Element[] elements;
	private final int numThreads;
	private Controller[] controllers;
	private Controller controllerType;
	
	public int genNum = 0;
	
	public void setReproductionType(Type t){
		this.reproductionType = t;
	}
	
	public void setMutationAmt(float mutationAmt){
		this.mutationAmt = mutationAmt;
	}
	
	public void setMutationRate(float mutationRate){
		this.mutationRate = mutationRate;
	}
	
	public void setFoundersPercent(float foundersPercent){
		this.foundersPercent = foundersPercent;
	}
	
	public void setGenerationMultiplier(int mult){
		this.mult = mult;
	}
	
	public void setGamesPerElement(int gamesPerElement){
		this.gamesPerElement = gamesPerElement;
	}

	public EvolutionAlgorithm(Simulation sim, Controller controller){
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
			
			//InsertionSort(elements);
			
			int numRepeat = 0;
			int split = elements.length - 1;
			while (controllerType.isSame(elements[elements.length - 1], elements[split]) && split >= 1) {
//				System.out.print("IsSame");
				numRepeat++;
				split--;
			}
//			System.out.println();
//			System.out.println(split);
			for (int j = split; j < elements.length - 1; j++) {
			
				if (numRepeat / (float) elements.length > foundersPercent) {
					elements[j] = controllerType.generateRandomConfig();
//					System.out.println("randomized");
				}
				else {
					elements[j] = reproduceFromArray(Arrays.copyOfRange(elements, 0, split));
//					System.out.println("reproduce from arr");
				}
			}
			
			/*for(Element e : elements)
				System.out.print(e.id + "\t");
			System.out.println();
			for(Element e : elements)
				System.out.print(e.getFitness() + "\t");
			System.out.println();*/
			

			if(Math.abs(elements[elements.length-1].getFitness()) < 0.1)
				System.out.println("Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
			
			if(genNum%500==0){
				System.out.println("Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
				System.out.println(genNum);
			}
			
			float curve = 2.0f;
			float x;
			Element.numElements = 0;
			Element[] nextGen = new Element[elements.length];
			
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			System.out.println("1Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
			//Elitism
			nextGen[0] = elements[elements.length-1];
			nextGen[0].setFitness(0);

			for(int i = 1; i < elements.length; i++)
				nextGen[i] = reproduceFromArray(elements);
			
			genNum++;
			elements = nextGen;
			
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
	
	private Element reproduceFromArray(Element[] els) {
		float curve = 1f;
		float x;
		
		float a = (float)(1 / Math.log(curve + 1) * elements.length);
		x = MathUtils.random();
		int e1Ind = (int)(Math.log(curve * x + 1) * a);
//		System.out.println(e1Ind);
		if (e1Ind == elements.length) e1Ind--;
		Element e1 = elements[e1Ind];
		int e2Ind;
		do{
			x = MathUtils.random();
			e2Ind = (int)(Math.log(curve * x + 1) * a);
			if (e2Ind == elements.length) e2Ind--;
		}while(e1Ind == e2Ind);
		Element e2 = elements[e2Ind];
		return reproduce(e1, e2);
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
	
	
	public static void InsertionSort(Element[] e){
	     boolean notDone = true;
	     while(notDone){
	    	 notDone = false;
	    	 //for(Element el : e){
	    		// System.out.print(el.id + ", ");
	    	 //}
	    	 //System.out.println();
		     for(int i = 0; i < e.length-1; i++){
		    	 if(e[i].getFitness() - 0.01 > e[i+1].getFitness()){
		    		 notDone = true;
		    		 Element tmp = e[i+1];
		    		 e[i+1]=e[i];
		    		 e[i] = tmp;
		    	 }
		     }
	     }
	}



}
