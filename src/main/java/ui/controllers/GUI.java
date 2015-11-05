package ui.controllers;

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

    public static void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
    	running = false;
    }
}
