package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import com.badlogic.gdx.math.MathUtils;

import evolver.Element;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;
import ui.Builder.InputFramework.EntryType;
import util.StringHolder;

public class EncogMLP extends Controller implements HasMenu {
	BasicNetwork net;
	Random r = new Random();
	
	@Override
	public double[] calculate(double... in) {
		double[] out = new double[net.getOutputCount()];
		net.compute(in, out);
		return out;
	}

	@Override
	public void setConfig(Element e) {
		element = e;
		net.decodeFromArray(e.config);
	}

	@Override
	public int getConfigSize() {
		return net.encodedArrayLength();
	}

	@Override
	public Element generateRandomConfig() {
		Element e = new Element();
		e.config = new double[net.encodedArrayLength()];
		for (int i = 0; i < net.encodedArrayLength(); i++) {
			e.config[i] = r.nextGaussian();
		}
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
		en.net = (BasicNetwork) net.clone();
		en.setInOut(this.numIn, this.numOut);
		HasMenu.migrate(inputF, en);
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

	InputFramework inputF = new InputFramework();
	StringHolder internalSize = new StringHolder("3, 3, 3");
	
	private int[] netDim;
	private int[] dims;
	
	@Override
	public void frameworkInit() {
		inputF.addEntry("Net Dim", EntryType.TEXT, internalSize, false);
	}

	@Override
	public InputFramework getFramework() {
		return inputF;
	}
	
	@Override
	public boolean check() {
		ArrayList<Integer> internalSizeArray;
		if(!inputF.checkAllInit())
			return false;
		
		String[] arr = internalSize.getValue().split(",");
		internalSizeArray = new ArrayList<Integer>(arr.length);
		for(int i = 0; i < arr.length; i++){
			if(arr[i].trim().length() > 0){
				try{
					internalSizeArray.add(Integer.parseInt(arr[i].trim()));
				}catch(Exception e){
					System.out.println("Error in numerical input");
					return false;
				}
			}
		}
		
		if(internalSizeArray.size() == 0)
			return false;
		
		netDim = new int[internalSizeArray.size()];
		for(int i = 0; i < internalSizeArray.size(); i++){
			this.netDim[i] = internalSizeArray.get(i);
		}
		
		return true;
	}
	
	@Override
	public void confirmMenu() {
		dims = calculateDimArray();
		
		net = new BasicNetwork();
		for (int i : dims)
			net.addLayer(new BasicLayer(i));
		net.getStructure().finalizeStructure();
		net.reset();
	}
	
	//Helper Methods:
	private int[] calculateDimArray(){
		int[] dims = new int[netDim.length + 2];
		dims[0] = numIn;
		dims[dims.length - 1] = numOut;
		for (int i = 1; i < dims.length - 1; i++)
			dims[i] = netDim[i - 1];
		return dims;
	}
	
}
