package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.GaussianRandomizer;

import com.badlogic.gdx.math.MathUtils;

import evolver.Element;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;
import ui.Builder.InputFramework.EntryType;
import util.*;

public class MLP extends Controller implements HasMenu{
	MultiLayerPerceptron mlpNet;
	private static GaussianRandomizer r = new GaussianRandomizer(0, 3);
	private TransferFunctionType f;
	private int[] netDim;
	private int[] dims;
	private InputFramework inputF;
	/*public MLP(TransferFunctionType f, int... netDim) {
		this.f = f;
		this.netDim = netDim;
		int[] dims = new int[netDim.length + 2];
		dims[0] = numIn;
		dims[dims.length - 1] = numOut;
		for (int i = 1; i < dims.length - 1; i++)
			dims[i] = netDim[i - 1];
		mlpNet = new MultiLayerPerceptron(f, dims);
	}*/

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
		element = e;
		mlpNet.setWeights(e.config);
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
	public void mutateElement(Element e, float mutateAmt) {
		for(int i = 0; i < mutateAmt * e.config.length; i++){
			e.config[(int) (MathUtils.random() * e.config.length)] = r.getRandomGenerator().nextDouble();
		}
	}

	@Override
	public Controller clone() {
		MLP c = new MLP();
		//System.out.println("Current In Out: " + this.numIn + "\t" + this.numOut);
		//System.out.println(f);
		c.setInOut(this.numIn, this.numOut);
		//System.out.println();
		//printInOut();
		//c.printInOut();
		//System.out.println();
		c.setNetworkDim(netDim);
		c.setTransferFunctionType(f);
		c.confirmMenu();
		return c;
	}
	
	@Override
	public boolean isSame(Element e1, Element e2) {
		double totalDist = 0;
		for (int i = 0; i < e1.config.length; i++)
			totalDist += Math.abs(e1.config[i] - e2.config[i]);
		totalDist /= e1.config.length;
//		System.out.println(totalDist);
		return totalDist < .10f;
		
	}

	
	@Override
	public InputFramework getFramework() {
		
		return inputF;
	}

	@Override
	public boolean check() {
		if(!inputF.checkAllInit())
			return false;
		
		String[] arr = internalSize.getValue().split(",");
		ArrayList<Integer> internalSizeArray = new ArrayList<Integer>(arr.length);
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
			
		f = ((TransferFunctionType)transferType.getFocusObject());

		
		return true;
	}

	@Override
	public void confirmMenu() {
		dims = calculateDimArray();
		mlpNet = new MultiLayerPerceptron(f, dims);
	}
	
	public void setTransferFunctionType(TransferFunctionType t){
		f = t;
	}
	
	public void setNetworkDim(int[] netDim){
		this.netDim = netDim;
	}
	
	private int[] calculateDimArray(){
		int[] dims = new int[netDim.length + 2];
		dims[0] = numIn;
		dims[dims.length - 1] = numOut;
		for (int i = 1; i < dims.length - 1; i++)
			dims[i] = netDim[i - 1];
		
		return dims;
		
	}

	
	StringHolder internalSize = new StringHolder("2,2");
	ComboHolder transferType = new ComboHolder(TransferFunctionType.values(),TransferFunctionType.TANH);
	@Override
	public void frameworkInit() {
		inputF = new InputFramework();
		inputF.addEntry("Net Dim", EntryType.TEXT, internalSize, false);
		inputF.addEntry("TransType", EntryType.COMBOBOX, transferType, false);
	}


	

}
