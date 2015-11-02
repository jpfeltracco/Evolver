package core;

import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import controllers.Controller;
import controllers.MLP;
import evolver.EvolutionAlgorithm;
import evolver.EvolutionAlgorithm.Type;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import simulations.Simulation;
import simulations.XOR;

public class Main extends Application {

	public static boolean runThreads = true;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
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

		
		
		System.out.println("Starting Simulation: " + s);
		new Thread(ea).start();
		
		Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Making new render window.");
                LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        		config.title = "Awesome";
        		config.width = 500;
        		config.height = 500;
        		config.resizable = false;
        		new LwjglApplication(new LibgdxMain(), config);
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
		
	}
	



}