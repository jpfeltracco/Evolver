package com.jeremyfeltracco.core.evolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.Main;
import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.simulations.Simulation;

public class EvolutionAlgorithm implements Runnable {

	public enum Type {
		HALF, RANDOM
	}

	private final Type t;
	private final int numPerGen;
	private final int mutationAmt;
	private final float mutationRate;
	private final Class sim;
	private final int controlInputs;
	private final int controlOutputs;
	private final int controlPerSim;
	private int availableControllers;
	private Element[] elements;
	private final int numThreads;
	private Controller[] controllers;
	private Controller c;

	@SuppressWarnings("unchecked")
	public EvolutionAlgorithm(Type t, int mult, int mutationAmt, float mutationRate, Class sim, Class controller)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.t = t;
		this.mutationAmt = mutationAmt;
		this.mutationRate = mutationRate;
		this.sim = sim;
		Simulation s = (Simulation)sim.newInstance();
		
		controlInputs = s.getNumInputs();
		controlOutputs = s.getNumOutputs();
		controlPerSim = s.getControlPerSim();
		
		Constructor<Controller> con = controller.getConstructor(int.class, int.class, TransferFunctionType.class, int[].class);
		c = con.newInstance(controlInputs, controlOutputs, TransferFunctionType.TANH, new int[] {3,3});
		availableControllers = c.getAvailableControllers();
		
		
		numThreads = availableControllers / controlPerSim; 
		numPerGen = mult * (numThreads * controlPerSim);
		
		System.out.println("numThreads: " + numThreads);
		System.out.println("controlPerSim: " + controlPerSim);
		System.out.println("NumPerGen: " + numPerGen);
		
		elements = new Element[numPerGen];
		
		//public MLP(int numIn, int numOut, TransferFunctionType f, int... netDim) {
		controllers = new Controller[availableControllers];
		controllers[0] = null;
		for(int i = 0; i < availableControllers; i++){
			con = null;
			try {
				con = controller.getConstructor(int.class, int.class, TransferFunctionType.class, int[].class);
				controllers[i] = con.newInstance(controlInputs, controlOutputs, TransferFunctionType.TANH, new int[] {3,3});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Element.numElements = 0;
		for (int i = 0; i < elements.length; i++)
			elements[i] = c.generateRandomConfig();
		
	}

	@Override
	public void run() {
		
		
		while (Main.runThreads) {
			// Setup simulations
			
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
				try {
					sims[i] = (Simulation)sim.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Controller[] appliedControllers = new Controller[controlPerSim];
				for(int j = 0; j < controlPerSim; j++)
					appliedControllers[j] = controllers[i*controlPerSim + j];
				
				sims[i].setControllers(appliedControllers);
				
				
				int val = appliedElements.length / numThreads;
				Element[] el = Arrays.copyOfRange(appliedElements, i*val, (i+1)*val);
				System.out.println("-----------------------------------------------");
				for(Element e : el)
					System.out.print(e.id + " ");
				System.out.println();
				
				sims[i].setElements(el);
				
				Simulation.simsRunning++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			//ACTIVATE THREAD (finish this portion
				//For Loop to do so (IE. run thread.start()
			for(Simulation s : sims)
				new Thread(s).start();
			
			waitForThreads();
			
			Arrays.sort(elements);
			Element.numElements = 0;
			float curve = 6.0f;
			float x;
			Element[] nextGen = new Element[elements.length];
			for(int i = 0; i < elements.length; i++){
				float a = (float)(1 / Math.log(curve + 1) * elements.length);
				x = MathUtils.random();
				Element e1 = elements[(int)(Math.log(curve * x + 1) * a)];
				x = MathUtils.random();
				Element e2 = elements[(int)(Math.log(curve * x + 1) * a)];
				
				nextGen[i] = reproduce(e1, e2);
			}
			
			elements = nextGen;
		}
	}
	
	
	private Element reproduce(Element e1, Element e2){
		Element tmp = new Element();
		double[] weights = new double[e1.config.length];
		
		int cut = e1.config.length/2;
		switch(t){
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
			c.mutateElement(tmp, mutationAmt);
		
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
