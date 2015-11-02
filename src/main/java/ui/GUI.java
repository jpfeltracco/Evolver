package ui;

import org.neuroph.util.TransferFunctionType;

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

    public static void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Genetic Algorithm Framework");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void startGA() {
    	Simulation s = new XOR();
        Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.SIN, 4, 4);
        EvolutionAlgorithm ea = new EvolutionAlgorithm(s, c);
        s.setEvolutionAlgorithm(ea);
        ea.setReproductionType(Type.RANDOM);
        ea.setGenerationMultiplier(10);
        ea.setMutationAmt(0.13f);
        ea.setMutationRate(0.15f);
        ea.setFoundersPercent(0.5f);
        ea.setGamesPerElement(5);
        eaThread = new Thread(ea);
        eaThread.start();
    }
    
    @Override
    public void stop() {
    	
    }
}
