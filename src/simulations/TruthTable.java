package simulations;

import controllers.Controller;
import ui.Builder.MenuItems;

public class TruthTable extends Simulation{

	int[][] vals = new int[][] {
		new int[] {0,0,0,0,1},
		new int[] {0,0,0,1,1},
		new int[] {0,0,1,0,0},
		new int[] {0,0,1,1,1},
		new int[] {0,1,0,0,0},
		new int[] {0,1,0,1,0},
		new int[] {0,1,1,0,0},
		new int[] {0,1,1,1,1},
		new int[] {1,0,0,0,0},
		new int[] {1,0,0,1,1},
		new int[] {1,0,1,0,1},
		new int[] {1,0,1,1,1},
		new int[] {1,1,0,0,0},
		new int[] {1,1,0,1,1},
		new int[] {1,1,1,0,1},
		new int[] {1,1,1,1,0}};
	
	@Override
	public double[] simulate(Controller[] c) {
		double[] actual = new double[vals[0].length-1];
		double answer;
		double err = 0;
		for(int[] i : vals){
			for(int j = 0; j < i.length-1; j++){
				if(i[j] == 0)
					actual[j] = -1;
				else
					actual[j] = 1;
			}
			
			if(i[i.length-1] == 0)
				answer = -1;
			else
				answer = 1;
			
			err += Math.abs(answer - c[0].calculate(actual)[0]);
		}
		return new double[] {-err};
	}

	@Override
	public int getNumInputs() {
		// TODO Auto-generated method stub
		return vals[0].length-1;
	}

	@Override
	public int getNumOutputs() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getControlPerSim() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Simulation copy() {
		// TODO Auto-generated method stub
		return new TruthTable();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Truth Table Simulation";
	}

	@Override
	public void menuInit(MenuItems menu) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return true;
	}

}
