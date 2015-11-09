package ui.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;

import controllers.Controller;
import evolver.EvolutionAlgorithm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import simulations.Simulation;
import ui.Builder.Builder;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;
import ui.graph.Graph;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class EaTabController {
	boolean startClicked;
	boolean pauseClicked;
	boolean status[] = new boolean[3];
	EvolutionAlgorithm ea = new EvolutionAlgorithm();
	Simulation simulation;
	Controller controller;
	Vector<Float> avgFit;
	final String tabID;
	final FXController fxController;
	final Tab tab;
	FXMLLoader fxmlLoader = new FXMLLoader();
	Builder builder = new Builder(this);
	public Graph grapher;
	final FileChooser fileChooser = new FileChooser();
	
	
	ArrayList<Series> graphSeries = new ArrayList<Series>();
	
	
	public EaTabController(String tabID, FXController fxController, Tab tab){
		this.tabID = tabID;
		this.fxController = fxController;
		this.tab = tab;
		//((HasMenu)ea).clearFramework();
		ea.frameworkInit();
		
		tab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
			public void handle(Event t) {
				if(close()){
					fxController.EATabsArray.set(fxController.EATabsArray.indexOf(t.getSource()), null);
					fxController.tabControllers.remove(this);
				}else
					t.consume();

			}
		});
		
		try {
			fxmlLoader.setController(this);
			Parent root = (Parent) fxmlLoader.load(this.getClass().getResource("eaTab.fxml").openStream());
			tab.setContent(root);
			fxController.tabControllers.add(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		simType.getItems().addAll(Simulation.getTypeOfSimulations());
		controllerType.getItems().addAll(Controller.getTypeOfControllers());
		
		startButtonEnable(false);
		pauseButton.setDisable(true);
		
		
		
		GridPane grid = getNewGrid();
		
		evolutionScrollPane.setContent(builder.build(ea, grid));
		simType.getSelectionModel().select(Simulation.getTypeOfSimulations()[0]);
		controllerType.getSelectionModel().select(Controller.getTypeOfControllers()[0]);
		
		builder.addNonChangable(simType);
		builder.addNonChangable(controllerType);
		
		accordion.setExpandedPane(evolutionPane);
		simPane.requestFocus();
		
		grapher = new Graph(fitnessGraph, this);
		ea.setGrapher(grapher);
		
		graphClean.setDisable(false);
		//ea.setEAController(this);
		
		//grapher.addSeries("Fitness");
		//grapher.addToSeries("Fitness", new Number[] {0,-1.5});
		//grapher.addToSeries("Fitness", new Number[] {1,-0.5});
		
		//fitnessGraph.setData(answer);
		//fitnessGraph.getData()
		
	}
	
	
	public void setController(Controller control){
		controller = control;
	}
	
	public synchronized void addToSeries(String name, Number[] vals){
		grapher.addToSeries(name, vals);
		//fitnessGraph.
	}
	public String getTabID(){
		return tabID;
	}
	
	public boolean close(){
		System.out.println("Closing tabID: " + tabID);
		ea.setRunning(false);
		return true;
	}
	
	//--------FXML--------//
	
	@FXML
	private Button startButton;
	
	@FXML
	private Button pauseButton;
	
	@FXML
	private Button renderButton;

	@FXML
	private ComboBox<String> simType;
	
	@FXML
	private ComboBox<String> controllerType;

	@FXML
	private LineChart<Number, Number> fitnessGraph;
	
	@FXML
	private TitledPane simPane;
	
	@FXML 
	private ScrollPane simScrollPane;
	
	@FXML
	private TitledPane controllerPane;
	
	@FXML 
	private ScrollPane controllerScrollPane;
	
	@FXML
	private TitledPane evolutionPane;
	
	@FXML
	private ScrollPane evolutionScrollPane;
	
	@FXML
	public CheckBox simulationValid;
	
	@FXML
	public CheckBox controllerValid;
	
	@FXML
	public CheckBox evolutionValid;
	
	@FXML
	private Button graphClean;
	
	@FXML
	public Label simLabel;
	
	@FXML
	public Label controlLabel;
	
	@FXML
	public Accordion accordion;
	
	@FXML
	private Label generationTag;
	
	public void setGeneration(int g){
		generationTag.setText("Generation: " + g);
	}
	
	@FXML
	private StackPane SimComboBox;
	
	@FXML
	private void onControllerTypeChanged(Event t){
		String val = (String)((ComboBox)t.getSource()).getValue();
		controller = (Controller) Controller.getController(val);
		//System.out.println(s.getSettings());
		
		controlLabel.setText(val);
		
		GridPane grid = getNewGrid();
		
        grid.add(controllerType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        
        if(controller instanceof HasMenu){
        	//((HasMenu)controller).clearFramework();
        	((HasMenu)controller).frameworkInit();
        	controllerScrollPane.setContent(builder.build(controller, grid));
        }else{
        	controllerValid.setSelected(true);
        	status[1] = true;
        	controllerScrollPane.setContent(grid);
        	checkValidity();
        }
        
        controllerPane.requestFocus();
	}
	
	
	int startFrom = 0;
	@FXML
	private void checkGraphClean(){
		if(grapher != null && grapher.size("Fitness")>4)
			grapher.simplifyData("Fitness", 2);
	}
	
	boolean orgnial = true;
	@FXML
	private void onSimTypeChanged(Event t) {
		if(!orgnial)
			return;
		String val = (String)((ComboBox)t.getSource()).getValue();
		simulation = (Simulation) Simulation.getSimulation(val);
		

		setSimulation(simulation);

		/*simLabel.setText(val);
		
		GridPane grid = getNewGrid();
		
        grid.add(simType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        if(simulation instanceof HasMenu){
        	//((HasMenu)simulation).clearFramework();
        	((HasMenu)simulation).frameworkInit();
        	simScrollPane.setContent(builder.build(simulation, grid));
        }else{
        	simulationValid.setSelected(true);
        	status[0] = true;
        	simScrollPane.setContent(grid);
        	checkValidity();
        }
		simPane.requestFocus();*/
	}
	
	public synchronized void setSimulationExt(Simulation sim){
		orgnial = false;
		setSimulation(sim);
		orgnial = true;
	}
	
	public synchronized void setSimulation(Simulation sim){
		System.out.println("Making Sim----------" + sim);
		this.simulation = sim;
		String val = sim.getClass().getName().substring(sim.getClass().getName().indexOf(".")+1);
		simLabel.setText(val);
		simType.getSelectionModel().select(val);
		GridPane grid = getNewGrid();
        grid.add(simType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        if(simulation instanceof HasMenu){
        	((HasMenu)simulation).frameworkInit();
        	simScrollPane.setContent(builder.build(simulation, grid));
        }else{
        	simulationValid.setSelected(true);
        	status[0] = true;
        	simScrollPane.setContent(grid);
        	checkValidity();
        }
		simPane.requestFocus();
	}
	
	public void startButtonEnable(boolean val){
		startButton.setDisable(!val);
	}
	
	@FXML
	private void onStartClicked() {
		
		// addEA();
		if (startClicked = !startClicked) {
			grapher.resetGraph();
			ea.setRunning(true);
			graphClean.setDisable(true);
			builder.setChangable(false);
			System.out.println("STARTING SIMULATION");
			System.out.println("SIM NUM IN: " + simulation.getNumInputs() + "\tNUM OUT: " + simulation.getNumOutputs());
			controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
			if(simulation instanceof HasMenu)
				((HasMenu)simulation).confirmMenu();
			if(controller instanceof HasMenu)
				((HasMenu)controller).confirmMenu();
			ea.setSimAndController(simulation,controller);
			((HasMenu)ea).confirmMenu();
			(new Thread(ea)).start();
			pauseButton.setDisable(false);
			startButton.setText("Stop");
		} else {
			ea.setRunning(false);
			builder.setChangable(true);
			graphClean.setDisable(false);
			InputFramework def = ea.getFramework();
			ea = new EvolutionAlgorithm();
			ea.frameworkInit();
			ea.getFramework().setDefaults(def);
			ea.setGrapher(grapher);
			GridPane grid = getNewGrid();
			evolutionScrollPane.setContent(builder.build(ea, grid));
			pauseButton.setDisable(true);
			startButton.setText("Start");
			pause = false;
			pauseButton.setText("Pause");
		}
	}
	
	@FXML 
	public void saveFitnessGraph(){
		saveChartAsPng(fitnessGraph);
	}
	
	public void saveChartAsPng(Chart graph) {
		// TODO: Review this process
	    WritableImage image = graph.snapshot(new SnapshotParameters(), null);

	    final DirectoryChooser directoryChooser = new DirectoryChooser();
	    final File selectedDirectory = directoryChooser.showDialog(GUI.stage);
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }
	    File file = new File(selectedDirectory.getAbsolutePath() + "/chart.png");

	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	    } catch (IOException e) {
	        // TODO: handle exception here
	    }
	}
	
	@FXML 
	public void saveFitnessGraphData(){
		saveChartAsXML(fitnessGraph);
	}
	
	public void saveChartAsXML(Chart graph) {
		// TODO: Review this process

		WritableImage image = graph.snapshot(new SnapshotParameters(), null);
	    final DirectoryChooser directoryChooser = new DirectoryChooser();
	    final File selectedDirectory = directoryChooser.showDialog(GUI.stage);
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }

	    File f = new File(selectedDirectory.getAbsolutePath() + "/output/chart.png");
	    
	    try {
			grapher.writeData(selectedDirectory);
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	
	boolean pause = false;
	@FXML
	private void onPauseClicked() {
		if (pause == false) {
			pause = true;
			ea.setRunning(false);
			
			pauseButton.setText("Resume");
		}else{
			pause = false;
			ea.setRunning(true);
			pauseButton.setText("Pause");
			(new Thread(ea)).start();
			
		}
	}
	
	
	public void setValidity(boolean val, Object section){
		
		if(section instanceof Simulation){
			simulationValid.setSelected(val);
			status[0] = val;
		}else if(section instanceof Controller){
			controllerValid.setSelected(val);
			status[1] = val;
		}else if(section instanceof EvolutionAlgorithm){
			evolutionValid.setSelected(val);
			status[2] = val;
		}
		
		
		checkValidity();
	}
	
	public void checkValidity(){
		boolean ready = true;
		for(boolean b : status){
			if(!b){
				ready = false;
				break;
			}
		}
		
		
		//System.out.println("READY: " + ready);
		startButtonEnable(ready);
	}
	
	private GridPane getNewGrid(){
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(2);
		
		ColumnConstraints c = new ColumnConstraints();
		c.setHgrow(Priority.SOMETIMES);
		c.setMaxWidth(173.0);
		c.setMinWidth(10.0);
		c.setPrefWidth(124.0);
		grid.getColumnConstraints().add(c);
		
		c = new ColumnConstraints();
		c.setHalignment(HPos.RIGHT);
		c.setHgrow(Priority.NEVER);
		grid.getColumnConstraints().add(c);
		
		RowConstraints r = new RowConstraints();
		r.setVgrow(Priority.NEVER);
		grid.getRowConstraints().add(r);
		
		return grid;
	}
	
	public void addSeries(ObservableList<Series<Number, Number>> s){
		fitnessGraph.setData(s);
	}
	
	//----------------------------------------------------------------------------

	/*public class Graph {
		LineChart<Number, Number> chart;
		ArrayList<String> graphSeriesTitles = new ArrayList<String>();
		ObservableList<Series<Number, Number>> graphSeries = FXCollections.observableArrayList();
		
		public Graph(LineChart<Number, Number> chart){
			this.chart = chart;
			chart.setData(graphSeries);
		}
		
		public synchronized void addSeries(String name){
			graphSeriesTitles.add(name);
			Series<Number, Number> s = new Series<Number, Number>();
			graphSeries.add(s);
		}
		
		public synchronized void addToSeries(String name, Number[] vals){
			getSeries(name).getData().add(new XYChart.Data<Number, Number>(vals[0],vals[1]));
		}
		
		private Series<Number,Number> getSeries(String name){
			for(int i = 0; i < graphSeriesTitles.size(); i++){
				if(graphSeriesTitles.get(i).equals(name))
					return graphSeries.get(i);
			}
			return null;
		}
		
		public synchronized void removeSeries(String name){
			for(int i = 0; i < graphSeriesTitles.size(); i++){
				if(graphSeriesTitles.get(i).equals(name)){
					graphSeriesTitles.remove(i);
					graphSeries.remove(i);
				}
			}
		}

		public synchronized void clearSeriesData(String name){
			getSeries(name).getData().clear();
		}
		
		public synchronized void clearAllData(){
			for(Series<Number, Number> s : graphSeries){
				s.getData().clear();
			}
		}
	}*/
	
	
	public void saveAll(){	
		WritableImage image = fitnessGraph.snapshot(new SnapshotParameters(), null);
		final DirectoryChooser directoryChooser = new DirectoryChooser();
	    File selectedDirectory = directoryChooser.showDialog(GUI.stage);
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }
	    String dir = selectedDirectory.getAbsolutePath();
	    File f = new File(dir + "/output/chart.png");
	    try {
			grapher.writeData(selectedDirectory);
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", f);
			
			FileOutputStream fileOut = new FileOutputStream(dir + "/output/controller.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(controller);
			out.close();
			fileOut.close();
			
			fileOut = new FileOutputStream(dir + "/output/simulation.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(simulation);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}


}
