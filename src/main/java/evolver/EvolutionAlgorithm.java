package evolver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;

import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import controllers.LimitedControllers;
import javafx.fxml.Initializable;
import simulations.Simulation;
import ui.Builder.Constraint;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;
import ui.Builder.InputFramework.EntryType;
import ui.controllers.GUI;
import util.*;

public class EvolutionAlgorithm implements HasMenu, Runnable {
	public boolean running = true;

	public enum Type {
		HALF, RANDOM
	}

	private Type reproductionType = Type.HALF;
	private float mutationAmt = 0.13f;
	private float mutationRate = 0.15f;
	private float foundersPercent = 0.5f;
	private int mult = 10;
	private int gamesPerElement = 5;
	private int numControllers = 5;
	
	private  int numPerGen;
	private  Simulation simType;
	private  int controlPerSim;
	private  int numThreads;
	
	private int availableControllers;
	private Element[] elements;
	
	private Controller[] controllers;
	private Controller controllerType;
	private InputFramework inputF;
	
	private Vector<Float> avgFit = new Vector<Float>();
	
	public int genNum = 0;
	
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}
	
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
	
	public void setPreferedNumberOfControllers(int i){
		numControllers = i;
	}
	
	public synchronized Vector<Float> getAvgFit() {
		return avgFit;
	}

	//public EvolutionAlgorithm(Simulation sim, Controller controller){
		//this.simType = sim;
		//this.controllerType = controller;
		
		//controlPerSim = simType.getControlPerSim();
		//availableControllers = controllerType.getAvailableControllers();
		
		
//		numThreads = availableControllers / controlPerSim; 
//		numPerGen = mult * (numThreads * controlPerSim);
//		
//		System.out.println("numThreads: " + numThreads);
//		System.out.println("controlPerSim: " + controlPerSim);
//		System.out.println("NumPerGen: " + numPerGen);
//		
//		elements = new Element[numPerGen];
//		
//		controllers = new Controller[availableControllers];
//
//		for(int i = 0; i < availableControllers; i++)
//			controllers[i] = controllerType.clone();
//		
//		Element.numElements = 0;
//		for (int i = 0; i < elements.length; i++)
//			elements[i] = controllerType.generateRandomConfig();
		
	//}
	
	/*private Type reproductionType = Type.HALF;
	private float mutationAmt = 0.13f;
	private float mutationRate = 0.15f;
	private float foundersPercent = 0.5f;
	private int mult = 10;
	private int gamesPerElement = 5;
	private int numControllers = 5;
	
	private  int numPerGen;
	private  Simulation simType;
	private  int controlPerSim;
	private  int numThreads;
	
	private int availableControllers;
	private Element[] elements;
	
	private Controller[] controllers;
	private Controller controllerType;
	private InputFramework inputF;
	 */
	

	@Override
	public void run() {
		System.out.println("EA started");
		System.out.println("Variable List:");
		System.out.println("\treproductionType: " + reproductionType);
		System.out.println("\tmutationAmt: " + mutationAmt);
		System.out.println("\tmutationRate: " + mutationRate);
		System.out.println("\tfoundersPercent: " + foundersPercent);
		System.out.println("\tmult: " + mult);
		System.out.println("\tgamesPerElement: " + gamesPerElement);
		System.out.println("\tnumControllers: " + numControllers);
		System.out.println("\tnumPerGen: " + numPerGen);
		System.out.println("\tsimType: " + simType);
		System.out.println("\tcontrollerType: " + controllerType);
		System.out.println("\tcontrolPerSim: " + controlPerSim);
		System.out.println("\tnumThreads: " + numThreads);
		System.out.println("\tavailableControllers: " + availableControllers);
		
		
		this.running = false;
		
		while (GUI.running && this.running) {
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
			
			if (elements[elements.length - 1].getFitness() > -1f) {
//				System.out.println("----");
				for(int i = 0; i < 3; i++){
					Simulation s = simType.clone();
					s.setControllers(new Controller[] {controllers[0]});
					s.setElements(new Element[] {elements[elements.length - 1]});
					s.verbose = false;
					new Thread(s).start();
				}
			}
			
			
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
			

//			if(Math.abs(elements[elements.length-1].getFitness()) < 0.1)
//				System.out.println("Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
//			
//			if(genNum%500==0){
//				System.out.println("Element: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness());
//				System.out.println(genNum);
//			}
			
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
			
			if(genNum%500 == 0){
				System.out.println("Gen: " + genNum + "\tElement: " + elements[elements.length-1].id + "\t Fitness: " + elements[elements.length-1].getFitness() + "\tReproduction: " + reproductionType);
			}
			//Elitism
			nextGen[0] = elements[elements.length-1];
			nextGen[0].setFitness(0);

			for(int i = 1; i < elements.length; i++)
				nextGen[i] = reproduceFromArray(elements);
			
			float totalFitness = 0;
			for (Element e : elements) {
				totalFitness += e.getFitness();
			}
			
			avgFit.add(totalFitness / elements.length);
			
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
		
		System.out.println("EA stopped");
		
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

	
	/*private Type reproductionType = Type.HALF;
	private float mutationAmt = 0.13f;
	private float mutationRate = 0.15f;
	private float foundersPercent = 0.5f;
	private int mult = 10;
	private int gamesPerElement = 5;*/
	
	
	
	public void setDefaults(InputFramework def){
		inputF.setDefaults(def);
	}
	
	@Override
	public InputFramework getFramework() {
		return inputF;
	}

	@Override
	public boolean check() {
		if(!inputF.checkAllInit())
			return false;
		System.out.println("\nReproduction:\t" + menuReproductionType.getFocusObject());
		System.out.println("M Amt:\t" + menuMutationAmt.getValue());
		System.out.println("M Rate:\t" + menuMutationRate.getValue());
		System.out.println("Founders:\t" + menuFoundersPercent.getValue());
		System.out.println("Mutiplier:\t" + menuMult.getValue());
		System.out.println("Games Per:\t" + menuGamePerElement.getValue());
		return true;
	}

	@Override
	public void confirmMenu() {
		setReproductionType((Type)menuReproductionType.getFocusObject());
		setMutationAmt((float)menuMutationAmt.getValue());
		setMutationRate((float)menuMutationRate.getValue());
		setFoundersPercent((float)menuFoundersPercent.getValue());
		setGenerationMultiplier(menuMult.getValue());
		setGamesPerElement(menuGamePerElement.getValue());
		setPreferedNumberOfControllers(preferedControllers.getValue());
		
		controlPerSim = simType.getControlPerSim();
		
		if(controllerType instanceof LimitedControllers){
			((LimitedControllers)controllerType).initializeControllerType();
			controllers = ((LimitedControllers)controllerType).checkout(numControllers);
		}else{
			availableControllers = numControllers;//controllerType.getAvailableControllers();
		}
		
		numThreads = availableControllers / controlPerSim; 
		numPerGen = mult * (numThreads * controlPerSim);
		
		//System.out.println("numThreads: " + numThreads);
		//System.out.println("controlPerSim: " + controlPerSim);
		//System.out.println("NumPerGen: " + numPerGen);
		
		elements = new Element[numPerGen];
		
		controllers = new Controller[availableControllers];

		for(int i = 0; i < availableControllers; i++)
			controllers[i] = controllerType.clone();
		
		Element.numElements = 0;
		for (int i = 0; i < elements.length; i++)
			elements[i] = controllerType.generateRandomConfig();
		
	}
	
	public void setSimAndController(Simulation sim, Controller controller){
		this.simType = sim;
		this.controllerType = controller;
		controlPerSim = sim.getControlPerSim();
	}
	
	ComboHolder menuReproductionType = new ComboHolder(Type.values(),Type.HALF);
	DoubleHolder menuMutationAmt = new DoubleHolder(0.13);
	DoubleHolder menuMutationRate = new DoubleHolder(0.15);
	DoubleHolder menuFoundersPercent = new DoubleHolder(0.5);
	IntegerHolder menuMult = new IntegerHolder(10);
	IntegerHolder menuGamePerElement = new IntegerHolder(5);
	IntegerHolder preferedControllers = new IntegerHolder(5);

	@Override
	public void frameworkInit() {
		System.out.println("Initialize!!");
		inputF = new InputFramework();
		inputF.addEntry("Reproduction", EntryType.COMBOBOX, menuReproductionType,false);
		inputF.addEntry("Mutation Amt", EntryType.SLIDER, menuMutationAmt, new Constraint(0,1,4),true);
		inputF.addEntry("Mutation Rate", EntryType.SLIDER, menuMutationRate, new Constraint(0,1,4),true);
		inputF.addEntry("Founders %", EntryType.SLIDER, menuFoundersPercent, new Constraint(0,1,4),true);
		inputF.addEntry("Mutiplier", EntryType.SLIDER, menuMult, new Constraint(1,100),false);
		inputF.addEntry("Games Per Element", EntryType.SLIDER, menuGamePerElement, new Constraint(1,100),false);
		inputF.addEntry("Controler #", EntryType.SLIDER, preferedControllers, new Constraint(1,25),false);
	}



}
