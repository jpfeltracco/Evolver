package evolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import controllers.LimitedControllers;
import simulations.Renderable;
import simulations.Simulation;
import ui.Builder.Constraint;
import ui.Builder.TabMenu;
import ui.Builder.MenuItems;
import ui.Builder.MenuItems.EntryType;
import ui.controllers.GUI;
import ui.graph.DataBridge;
import util.*;

public class EvolutionAlgorithm extends TabMenu implements Runnable {
	public boolean running = true;

	public enum Type {
		HALF, RANDOM, NONE
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
	
	private Element bestElement;
	
	private DataBridge dataBridge;
	private boolean renderNextGen = false;
	
	private Vector<Float> avgFit = new Vector<Float>();
	
	public int genNum = 0;
	
	public void setGrapher(DataBridge g){
		dataBridge = g;
	}
	
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
	
	ArrayList<Double> runningAvg = new ArrayList<Double>();
	int startFrom = 0;
	
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
		
		check();
		
		
		//this.running = false;
		
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
			
			System.out.println(elementHolder.size());

			
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
				numRepeat++;
				split--;
			}
			
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
			
			Element.numElements = 0;
			Element[] nextGen = new Element[elements.length];
			
			runningAvg.add(elements[elements.length-1].getFitness());
			
			bestElement = elements[elements.length-1];
			
			if(genNum%graphAmt.getValue() == 0){	

				dataBridge.graphData("Fitness", new Number[] {genNum, bestElement.getFitness()});
				runningAvg.clear();
				
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
			
			dataBridge.setGeneration(genNum);
			elements = nextGen;
			
			if(dataBridge.isVirtual())
				dataBridge.check();
			
			int delay = delayAmt.getValue();
			
			if(delay != 0){
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
		
		System.out.println("EA stopped");
		
	}
	
	public synchronized void renderBest() {
		this.running = false;
		if (simType instanceof Renderable) {
			Simulation watchSim = simType.copy();
			watchSim.setControllers(controllers);
		}
	}
	
	public synchronized Controller getBestElement(){
		System.out.println(bestElement);
		controllerType.setConfig(bestElement);
		return controllerType;
	}
	
	public synchronized ElementHolder getExportedElements(){
		return new ElementHolder(elements, genNum);
	}
	
	boolean setValues = false;
	public void readElementHolder(ElementHolder eh){
		if(eh != null && eh.getElements() != null){
			elements = eh.getElements();
			Element.numElements = elements.length;
			setValues = true;
			genNum = eh.getGen();
		}
		
	}
	
	private double avg(ArrayList<Double> points){
		double sum = 0;
		for(Double d : points){
			sum += d;
		}
		double out = sum / points.size();
		//System.out.println(out);
		return out;
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
		return reproduce(e1.clone(), e2.clone());
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
		case NONE:
			weights = e1.config;
			break;
		}
		tmp.config = weights;
		
		if (MathUtils.random() < mutationRate)
			controllerType.mutateElement(tmp, mutationAmt);
		
		return tmp;
	}
	
	@Override
	public synchronized boolean check() {
		if(!menuItems.checkAllInit())
			return false;
		System.out.println("\nReproduction:\t" + menuReproductionType.getFocusObject());
		System.out.println("\nReproduction:\t" + menuReproductionType.getFocusObject());
		System.out.println("\nReproduction:\t" + menuReproductionType.getFocusObject());
		System.out.println("M Amt:\t" + menuMutationAmt.getValue());
		System.out.println("M Rate:\t" + menuMutationRate.getValue());
		System.out.println("Founders:\t" + menuFoundersPercent.getValue());
		System.out.println("Delay:\t" + delayAmt.getValue());
		System.out.println("Graph Amt:\t" + graphAmt.getValue());
		return true;
	}

	@Override
	public void start() {
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
		
		controllers = new Controller[availableControllers];
		for(int i = 0; i < availableControllers; i++)
			controllers[i] = controllerType.clone();
		
		if(!setValues){
			elements = new Element[numPerGen];			
			Element.numElements = 0;
			for (int i = 0; i < elements.length; i++)
				elements[i] = controllerType.generateRandomConfig();
		}
		
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
	IntegerHolder menuGamePerElement = new IntegerHolder(40);
	IntegerHolder preferedControllers = new IntegerHolder(15);
	IntegerHolder graphAmt = new IntegerHolder(10);
	
	IntegerHolder delayAmt = new IntegerHolder(0);

	@Override
	public void menuInit(MenuItems inputF) {
		inputF.add("Reproduction", EntryType.COMBOBOX, menuReproductionType,false);
		inputF.add("Delay", EntryType.SLIDER, delayAmt, new Constraint(0,1000), true);
		inputF.add("Graph Frequancy (/GEN)", EntryType.SLIDER, graphAmt, new Constraint(1,1000),true);
		inputF.add("Mutation Amt", EntryType.SLIDER, menuMutationAmt, new Constraint(0,1,4),true);
		inputF.add("Mutation Rate", EntryType.SLIDER, menuMutationRate, new Constraint(0,1,4),true);
		inputF.add("Founders %", EntryType.SLIDER, menuFoundersPercent, new Constraint(0,1,4),true);
		inputF.add("Mutiplier", EntryType.SLIDER, menuMult, new Constraint(1,100),false);
		inputF.add("Games Per Element", EntryType.SLIDER, menuGamePerElement, new Constraint(1,100),false);
		inputF.add("Controller #", EntryType.SLIDER, preferedControllers, new Constraint(1,25),false);
	}




}
