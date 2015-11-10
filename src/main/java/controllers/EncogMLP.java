package controllers;

import java.io.File;
import java.util.Random;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import com.badlogic.gdx.math.MathUtils;

import evolver.Element;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;

public class EncogMLP extends Controller implements HasMenu {
	BasicNetwork net;
	Random r = new Random();
	int size = 0;
	
	@Override
	public double[] calculate(double... in) {
		double[] out = new double[net.getOutputCount()];
		net.compute(in, out);
		return out;
	}

	@Override
	public void setConfig(Element e) {
		double[] weights = new double[size];
		net.encodeToArray(weights);
		e.config = weights;
	}

	@Override
	public int getConfigSize() {
		return size;
	}

	@Override
	public Element generateRandomConfig() {
		double[] out = new double[size];
		for (int i = 0; i < size; i++) {
			out[i] = r.nextGaussian();
		}
		Element e = new Element();
		e.config = out;
		return e;
	}

	// TODO both mlps will share this behavior
	@Override
	public void mutateElement(Element e, float mutateAmt) {
		for(int i = 0; i < mutateAmt * e.config.length; i++){
			e.config[(int) (MathUtils.random() * e.config.length)] = r.nextGaussian();
		}
	}

	@Override
	public Controller clone() {
		EncogMLP en = new EncogMLP();
		en.size = size;
		en.net = (BasicNetwork) net.clone();
		return en;
	}

	// TODO same as other mlp, put in superclass
	@Override
	public boolean isSame(Element e1, Element e2) {
		double totalDist = 0;
		for (int i = 0; i < e1.config.length; i++)
			totalDist += Math.abs(e1.config[i] - e2.config[i]);
		totalDist /= e1.config.length;
		return totalDist < .10f;
	}

	@Override
	public void saveConfig(File loc) {
		// TODO Auto-generated method stub
	}

	@Override
	public String[] getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void frameworkInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputFramework getFramework() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void confirmMenu() {
		net = new BasicNetwork();
		net.addLayer(new BasicLayer(2));
		net.addLayer(new BasicLayer(3));
		net.addLayer(new BasicLayer(1));
		net.getStructure().finalizeStructure();
		net.reset();
		
		size = net.calculateNeuronCount();
	}
	
}
