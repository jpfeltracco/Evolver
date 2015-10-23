package com.jeremyfeltracco.core.controllers;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import com.jeremyfeltracco.core.evolver.Element;

public class MLP extends Controller {
	MultiLayerPerceptron mlpNet;
	
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
		return 255;
	}

}
