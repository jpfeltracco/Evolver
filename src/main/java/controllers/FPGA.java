package controllers;

import evolver.Element;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;

public class FPGA extends Controller implements HasMenu, LimitedControllers{

	@Override
	public void initializeControllerType() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxNumberOfControllers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Controller[] checkout(int preferedNumControllers) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public double[] calculate(double... in) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfig(Element e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getConfigSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Element generateRandomConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mutateElement(Element e, float mutateAmt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Controller clone() {
		return null;
	}

	@Override
	public boolean isSame(Element e1, Element e2) {
		// TODO Auto-generated method stub
		return false;
	}



}
