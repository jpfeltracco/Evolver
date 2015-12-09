package testers;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import controllers.Controller;
import controllers.MLP;
import javafx.stage.FileChooser;
import simulations.HigherOrLower;
import ui.controllers.GUI;

public class HigherOrLowerTester {
	
	static Random randomGenerator = new Random();
	
	public HigherOrLowerTester(){
		HigherOrLower sim = new HigherOrLower();
		MLP mlp = new MLP();
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter file loc: ");
		String in = scan.nextLine();
		
		mlp.loadConfig(new File(in));
		sim.simulate(new Controller[] {mlp});
		
		
		
		int trialCount = 0;
		int rightCount = 0;
		//double scale = 2/((double)upperBound.getInt() - (double)lowerBound.getInt());
		while(true){
			
			int a = 51;//randomGenerator.nextInt(100);//scan.nextInt();
			int b = 49;//randomGenerator.nextInt(100);//scan.nextInt();
			System.out.println("A: " + a + "\tB: " + b);
			
			boolean aBiggest;
			int k;
			do{
				k = randomGenerator.nextInt(100);
			}while (k == a);
			k=100;
			//System.out.println(a + "\t" + convert(a));
			
			aBiggest = mlp.calculate(convert(a), convert(k))[0] >= 0;
			System.out.println("K: " + k + "\tGuess: "+ a + ((aBiggest)?" > ":" < ") + b);


			if((aBiggest && a > b) || (!aBiggest && a < b)){
				rightCount += 1;
				System.out.print("Correct\t");
			}else{
				System.out.print("Wrong\t");
			}

			trialCount++;
			System.out.println(((rightCount / (double)trialCount)*100) + "%\n");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static double convert(double x){
    	return (2*(x - 0)) / (100 - 0) -1;
    }
}
