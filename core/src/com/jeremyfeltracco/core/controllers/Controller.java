package com.jeremyfeltracco.core.controllers;

import com.jeremyfeltracco.core.evolver.Element;

public abstract class Controller {
	int numIn;
	int numOut;
	double[] output;
	
	public Controller(int numIn, int numOut) {
		this.numIn = numIn;
		this.numOut = numOut;
		output = new double[numOut];
	}
	
	public abstract double[] calculate(double... in);
	
	public abstract void setConfig(Element e);
	
	public abstract int getAvailableControllers();
	
	public abstract int getConfigSize();
	
	public abstract Element generateRandomConfig();
	
	public abstract void mutateElement(Element e, float mutateAmt);
	
	public abstract Controller clone();
}
