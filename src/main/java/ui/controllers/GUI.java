package ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.Gdx;

import controllers.Controller;
import controllers.MLP;
import evolver.EvolutionAlgorithm;
import evolver.EvolutionAlgorithm.Type;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulations.Simulation;
import simulations.XOR;

/**
 * Created by jpfel on 11/2/2015.
 */
public class GUI extends Application {
	
	Thread eaThread;
	public static Stage stage;
	public static boolean running = true;
	public static File lastFileSystemLoc = new File(System.getProperty("user.dir") + "/assets/fileloc.dat");
	public static File lastFileLocation;
	
    public static void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	
    	initFiles();
    	
    	
    	
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

        Scene scene = new Scene(root, 750, 600);
        stage = primaryStage;
        primaryStage.setTitle("Genetic Algorithm Framework");
        primaryStage.setScene(scene);
        primaryStage.show();
        //addEA();
    }
    
    @Override
    public void stop() {
    	System.out.println("GUI Stop");
    	setFileLoc();
    	running = false;
    }
    
    private void initFiles(){
    	if(!lastFileSystemLoc.exists()){
			lastFileLocation = new File(System.getProperty("user.home"));
			try {
				PrintWriter pw = new PrintWriter(lastFileSystemLoc);
				pw.write(lastFileLocation.getAbsolutePath());
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			try {
				FileReader fr = new FileReader(lastFileSystemLoc);
				BufferedReader br = new BufferedReader(fr);
				//System.out.println(br.readLine());
				lastFileLocation = new File(br.readLine());
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e2){
				e2.printStackTrace();
			}
		}
    }
    
    public void setFileLoc(){
		try {
			PrintWriter pw = new PrintWriter(lastFileSystemLoc);
			pw.write(lastFileLocation.getAbsolutePath());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
