package ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

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
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simulations.Simulation;
import simulations.XOR;
import ui.terminal.Console;

/**
 * Created by jpfel on 11/2/2015.
 */
public class GUI extends Application {
	
	Thread eaThread;
	public static Stage stage;
	public static boolean running = true;
	public static File lastFileLocation;
	public static String lastFileName;
	public URL fileLocURL = getClass().getResource("/assets/fileloc.dat");
	
	private static final int MAXRECENTFILES = 7;
	static ArrayList<File> recentFiles = new ArrayList<File>(MAXRECENTFILES + 1);
	public static String[] allowedExts = new String[] {".evs", ".evo"};
	
    public static void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	
    	initFiles();
    	
    	Console terminal = new Console();
    	
    	
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

        Scene scene = new Scene(root, 750, 600); 
        stage = primaryStage;
        
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        
        try {
			Image img = new Image(new FileInputStream(getClass().getResource("/assets/icon.png").getPath()));
			GUI.stage.getIcons().add(img);
			if(System.getProperty("os.name").contains("Mac")){
				java.awt.Image img2 = new ImageIcon(getClass().getResource("/assets/icon.png").getPath()).getImage();
//				com.apple.eawt.Application.getApplication().setDockIconImage(img2);
//				com.apple.eawt.Application.getApplication().setDockIconBadge("Testing");
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
			String pathName = fileLocURL.getPath();
			//if(pathName.indexOf("!") != -1)
				//pathName = pathName.substring(0, pathName.indexOf("!")) + pathName.substring(pathName.indexOf("!") + 1);
			FileReader fr = new FileReader(pathName);
			BufferedReader br = new BufferedReader(fr);
			String path = br.readLine();
			lastFileName = br.readLine();
			System.out.println(path + "\t" +  lastFileName);
			if(path == null || path.length() == 0){
				lastFileLocation = new File(System.getProperty("user.home"));
			}else{
				lastFileLocation = new File(path);
				if(!lastFileLocation.exists()){
					lastFileLocation = new File(System.getProperty("user.home"));
				}
			}
			String line;
			while((line = br.readLine()) != null){
				File tmp = new File(line.trim());
				if(tmp.exists())// && checkExt(tmp.getName().substring(tmp.getName().indexOf(".")), allowedExts))
					addRecentFile(tmp);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2){
			e2.printStackTrace();
		}
		
    	
  
    	
    	
    }
    
    public void setFileLoc(){
		
		try (PrintWriter pw = new PrintWriter(fileLocURL.getPath())) {
			pw.println(lastFileLocation.getAbsolutePath());
			if(lastFileName != null)
				pw.println(lastFileName);
			else
				pw.println();
			
			for(File f : recentFiles){
				System.out.println(f.getAbsolutePath());
				pw.println(f.getAbsolutePath());
			}
	    } catch (IOException e) {
	        e.printStackTrace();

	    }
	}
    
    public static boolean checkExt(String ext, String[] exts){
		for(String s : exts){
			if(s.equals(ext))
				return true;
		}
		return false;
	}
    
    public static String removeMenuItem;
    public static MenuItem addRecentFile(File f){
    	removeMenuItem = null;
    	for(File file : recentFiles)
    		if(file.getAbsolutePath().equals(f.getAbsolutePath()))
    			return null;
    	System.out.println("Added: " + f.getAbsolutePath());
    	recentFiles.add(f);
    	if(recentFiles.size() > MAXRECENTFILES){
    		System.out.println("Removed: " + recentFiles.get(0).getAbsolutePath());
    		removeMenuItem = recentFiles.get(0).getAbsolutePath();
    		recentFiles.remove(0);
    	}
    	MenuItem m = new MenuItem();
		m.setText(f.getAbsolutePath());
		return m;
    }
    
    public static String getRemovedItem(){
    	return removeMenuItem;
    }
    
 
    
}
