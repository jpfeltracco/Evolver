package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.GaussianRandomizer;

import com.badlogic.gdx.math.MathUtils;

import evolver.Element;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;
import ui.Builder.InputFramework.EntryType;
import util.ComboHolder;
import util.StringHolder;

public class MLP extends Controller implements HasMenu{
	MultiLayerPerceptron mlpNet;
	private static Random r = new Random();
	
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
		for (int j = 0; j < getConfigSize(); j++) {
//			e.config[j] = r.getRandomGenerator().nextDouble();
			e.config[j] = r.nextGaussian();
			System.out.println(e.config[j]);
		}	
		return e;
	}

	@Override
	public void mutateElement(Element e, float mutateAmt) {
		for(int i = 0; i < mutateAmt * e.config.length; i++){
			e.config[(int) (MathUtils.random() * e.config.length)] = r.nextGaussian();
		}
	}

	@Override
	public Controller clone() {
		MLP c = new MLP();
		c.setInOut(this.numIn, this.numOut);
		HasMenu.migrate(inputF, c);
		return c;
	}
	
	@Override
	public boolean isSame(Element e1, Element e2) {
		double totalDist = 0;
		for (int i = 0; i < e1.config.length; i++)
			totalDist += Math.abs(e1.config[i] - e2.config[i]);
		totalDist /= e1.config.length;
		return totalDist < .10f;
		
	}
	
	//------------------------------------------------------------------------------------------
	//----------------------------------------MENU----------------------------------------------
	//------------------------------------------------------------------------------------------
	
	//Initializations:
	InputFramework inputF = new InputFramework();
	StringHolder internalSize = new StringHolder("3, 3, 3");
	ComboHolder transferType = new ComboHolder(TransferFunctionType.values(),TransferFunctionType.TANH);
	
	private TransferFunctionType f;
	private int[] netDim;
	private int[] dims;


	@Override
	public void frameworkInit() {
		inputF.addEntry("Net Dim", EntryType.TEXT, internalSize, false);
		inputF.addEntry("TransType", EntryType.COMBOBOX, transferType, false);
	}

	@Override
	public InputFramework getFramework() {
		return inputF;
	}
	
	ArrayList<Integer> internalSizeArray;
	@Override
	public boolean check() {
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
		System.out.println("\n--------------NEW MLP--------------");
		dims = calculateDimArray();
		f = ((TransferFunctionType)transferType.getFocusObject());
		System.out.print("Dim Array:\t");
		for(int s : dims){
			System.out.print("" + s + " ");
		}
		System.out.println("\nTransferType:\t" + f);
		mlpNet = new MultiLayerPerceptron(f, dims);
		System.out.println("-----------------------------------\n");
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

	@Override
	public void saveConfig(File loc) {
		System.out.println(loc.getAbsolutePath());
		mlpNet.save(loc.getAbsolutePath());
	}

	@Override
	public String[] getExtension() {
		return new String[] {"nnet"};
	}

	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	

}
