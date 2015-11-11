package ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.ImageIcon;

import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.Gdx;

import controllers.Controller;
import controllers.MLP;
import core.Main;
import evolver.EvolutionAlgorithm;
import evolver.EvolutionAlgorithm.Type;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
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
	public static File lastFileLocation;
	
    public static void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	
    	initFiles();
    	
    	//TESTING
    	
    	
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

        Scene scene = new Scene(root, 750, 600);
        stage = primaryStage;
        
        try {
			Image img = new Image(new FileInputStream(getClass().getResource("/assets/icon.png").getPath()));
			GUI.stage.getIcons().add(img);
			if(System.getProperty("os.name").contains("Mac")){
				java.awt.Image img2 = new ImageIcon(getClass().getResource("/assets/icon.png").getPath()).getImage();
				com.apple.eawt.Application.getApplication().setDockIconImage(img2);
				com.apple.eawt.Application.getApplication().setDockIconBadge("Testing");
			}
			//com.apple.eawt.Application.getApplication().
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
       
        //menuBar.setUseSystemMenuBar(true);
        
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
    	//System.out.println("FILE: " + getClass().getResource("/assets/fileloc.dat").toString());
    	//System.out.println("EXISTS: " + lastFileSystemLoc.exists());
    	
    	
		try {
			String pathName = getClass().getResource("/assets/fileloc.dat").getPath();
			//System.out.println("POINTS: " + pathName.indexOf("!"));
			//if(pathName.indexOf("!") != -1)
				//pathName = pathName.substring(0, pathName.indexOf("!")) + pathName.substring(pathName.indexOf("!") + 1);
			FileReader fr = new FileReader(pathName);
			BufferedReader br = new BufferedReader(fr);
			String path = br.readLine();
			if(path == null || path.length() == 0){
				lastFileLocation = new File(System.getProperty("user.home"));
			}else{
				lastFileLocation = new File(path);
				if(!lastFileLocation.exists()){
					lastFileLocation = new File(System.getProperty("user.home"));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2){
			e2.printStackTrace();
		}
		
    	
  
    	
    	
    }
    
    public void setFileLoc(){
		try {
			PrintWriter pw = new PrintWriter(getClass().getResource("/assets/fileloc.dat").getPath());
			pw.write(lastFileLocation.getAbsolutePath());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
    
}
