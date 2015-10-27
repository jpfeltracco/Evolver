package com.jeremyfeltracco.core.controllers;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.GaussianRandomizer;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.evolver.Element;

public class MLP extends Controller {
	MultiLayerPerceptron mlpNet;
	private static GaussianRandomizer r = new GaussianRandomizer(0, 1);
	
	public MLP(int numIn, int numOut, TransferFunctionType f, int... netDim) {
		super(numIn, numOut);
		int[] dims = new int[netDim.length + 2];
		dims[0] = numIn;
		dims[dims.length - 1] = numOut;
		for (int i = 1; i < dims.length - 1; i++)
			dims[i] = netDim[i - 1];
		mlpNet = new MultiLayerPerceptron(f, dims);
	}

	@Override
	public double[] calculate(double... in) {
		mlpNet.setInput(in);
		mlpNet.calculate();
		return mlpNet.getOutput();
	}
	
	@Override
	public int getConfigSize() {
		return mlpNet.getWeights().length;
	}

	@Override
	public void setConfig(Element e) {
		mlpNet.setWeights(e.config);
	}

	@Override
	public int getAvailableControllers() {
		return 5;
	}

	@Override
	public Element generateRandomConfig() {
		Element e = new Element();
		e.config = new double[getConfigSize()];
		for (int j = 0; j < getConfigSize(); j++)
			e.config[j] = r.getRandomGenerator().nextDouble();
		return e;
	}

	@Override
	public void mutateElement(Element e, int mutateAmt) {
		for(int i = 0; i < mutateAmt; i++){
			e.config[(int) (MathUtils.random() * e.config.length)] = r.getRandomGenerator().nextDouble();
		}
	}

}
