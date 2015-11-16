package controllers;

import java.io.File;

import evolver.Element;
import ui.Builder.MenuItems;

public class FPGA extends Controller {

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
	public Controller copy() {
		return new FPGA();
	}

	@Override
	public boolean isSame(Element e1, Element e2) {
		// TODO Auto-generated method stub
		return false;
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
	public void start(int numIn, int numOut) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void menuInit(MenuItems inputF) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		return false;
	}



}
