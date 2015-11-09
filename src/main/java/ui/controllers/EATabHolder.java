package ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
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

public abstract class EATabHolder {
	
	final String tabID;
	final Tab tab;
	final FXController fxController;
	final String FXMLTYPE = "eaTab.fxml";
	public Graph fitnessGrapher;
	final FileChooser fileChooser = new FileChooser();
	Builder builder = new Builder(this);
	EvolutionAlgorithm ea = new EvolutionAlgorithm();
	boolean original = true;
	
	Simulation simulation;
	Controller controller;
	
	boolean status[] = new boolean[3];
	
	public EATabHolder(String tabID, Tab tab, FXController fxController){
		this.tabID = tabID;
		this.tab = tab;
		this.fxController = fxController;
		
		this.simulation = Simulation.getSimulation(Simulation.getTypeOfSimulations()[0]);
		this.controller = Controller.getController(Controller.getTypeOfControllers()[0]);
		
		initializeTab();
		setEvolutionSection();
		
	}
	
	public EATabHolder(String tabID, Tab tab, FXController fxController, Simulation simulation, Controller controller, InputFramework inputF, ElementHolder elements, File directory){
		this.tabID = tabID;
		this.tab = tab;
		this.fxController = fxController;
		this.simulation = simulation;
		this.controller = controller;
		
		ea.getFramework().clear();
		ea.frameworkInit();
		ea.getFramework().setDefaults(inputF);
		ea.readElementHolder(elements);
		
		initializeTab();
		
		
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		};
		
		File[] files = directory.listFiles(textFilter);
		for (File file : files) {
			if (!file.isDirectory()) {
				String sCurrentLine;
				BufferedReader br;
				StringTokenizer st;
				String series = file.getName().substring(0, file.getName().indexOf("."));
				fitnessGrapher.addSeries(series);
				try {
					br = new BufferedReader(new FileReader(file.getAbsolutePath()));
					sCurrentLine = br.readLine();
					while ((sCurrentLine = br.readLine()) != null) {
						st = new StringTokenizer(sCurrentLine, ",");
						fitnessGrapher.addToSeries(series, new Number[] {Integer.parseInt(st.nextToken()) , Double.parseDouble(st.nextToken())});
						fitnessGrapher.setLoaded(true);
					}
					br.close();
				} catch (IOException e) {
					throw new RuntimeException("There was an error opening the Elements: " + e);
				}
			}
		}
		
		GridPane grid = getNewGrid();
		evolutionScrollPane.setContent(builder.build(ea, grid));
		
	}
	
	public void setEvolutionSection(){
		GridPane grid = getNewGrid();
		ea.frameworkInit();
		evolutionScrollPane.setContent(builder.build(ea, grid));
	}
	
	public void initializeTab(){
		//Sets the master Tab to close when this thing is ready, and not before.
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
		
		//Populate this Tab with the FXML listed.
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.setController(this);
			Parent root = (Parent) fxmlLoader.load(this.getClass().getResource(FXMLTYPE).openStream());
			tab.setContent(root);
			fxController.tabControllers.add(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Set up grapher
		fitnessGrapher = new Graph(fitnessGraph, this);
		ea.setGrapher(fitnessGrapher);
		
		//Set the initial button enables
		startButton.setDisable(true);
		pauseButton.setDisable(true);
		graphClean.setDisable(false);
			
		//Sets Non-changeable items
		builder.addNonChangable(simType);
		builder.addNonChangable(controllerType);
			
		//Sets the Evolution Section
		accordion.setExpandedPane(evolutionPane);
		
		//Set the Simulation and Controller drop downs
		simType.getItems().addAll(Simulation.getTypeOfSimulations());
		controllerType.getItems().addAll(Controller.getTypeOfControllers());
	}
	
	public String getTabID(){
		return tabID;
	}
	
	public void saveAll(ElementHolder elementHolder){	
		WritableImage image = fitnessGraph.snapshot(new SnapshotParameters(), null);
		final DirectoryChooser directoryChooser = new DirectoryChooser();
	    File selectedDirectory = directoryChooser.showDialog(GUI.stage);
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }
	    String dir = selectedDirectory.getAbsolutePath();
	    File f = new File(dir + "/output/chart.png");
	    try {
			fitnessGrapher.writeData(selectedDirectory);
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
			
			fileOut = new FileOutputStream(dir + "/output/evolve.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(ea.getFramework());
			out.close();
			fileOut.close();
			
			fileOut = new FileOutputStream(dir + "/output/elements.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(elementHolder);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
			fitnessGrapher.writeData(selectedDirectory);
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
			
			fileOut = new FileOutputStream(dir + "/output/evolve.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(ea.getFramework());
			out.close();
			fileOut.close();
			
			fileOut = new FileOutputStream(dir + "/output/elements.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(ea.getExportedElements());
			out.close();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		startButton.setDisable(!ready);
	}
	
	protected GridPane getNewGrid(){
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
	
	@FXML
	protected void onControllerTypeChanged(Event t){
		if(!original){
			t.consume();
			return;
		}
		String val = (String)controllerType.getValue();
		controller = (Controller) Controller.getController(val);
		changeController(controller);
	}
	
	@FXML
	protected void onSimTypeChanged(Event t){
		if(!original){
			t.consume();
			return;
		}
		String val = (String)simType.getValue();
		simulation = (Simulation) Simulation.getSimulation(val);
		changeSim(simulation);
	}
	
	@FXML
	protected void checkGraphClean(){
		if(fitnessGrapher != null && fitnessGrapher.size("Fitness")>4)
			fitnessGrapher.simplifyData("Fitness", 2);
	}
	
	@FXML
	public void saveFitnessGraphData(){
		saveChartAsXML(fitnessGraph);
	}
	
	public void setGeneration(int g){
		generationTag.setText("Generation: " + g);
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
			fitnessGrapher.writeData(selectedDirectory);
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	
	//Abstract methods -----------------------------------------------------------
	@FXML
	protected abstract void saveFitnessGraph();
	
	protected abstract void changeSim(Simulation sim);
	
	protected abstract void changeController(Controller control);
	
	public abstract boolean close();
	
	@FXML
	protected abstract void onStartClicked();
	
	@FXML
	protected abstract void onPauseClicked();
	
	@FXML
	protected Button startButton;
	
	@FXML
	protected Button pauseButton;
	
	@FXML
	protected Button renderButton;

	@FXML
	protected ComboBox<String> simType;
	
	@FXML
	protected ComboBox<String> controllerType;

	@FXML
	protected LineChart<Number, Number> fitnessGraph;
	
	@FXML
	protected TitledPane simPane;
	
	@FXML 
	protected ScrollPane simScrollPane;
	
	@FXML
	protected TitledPane controllerPane;
	
	@FXML 
	protected ScrollPane controllerScrollPane;
	
	@FXML
	protected TitledPane evolutionPane;
	
	@FXML
	protected ScrollPane evolutionScrollPane;
	
	@FXML
	public CheckBox simulationValid;
	
	@FXML
	public CheckBox controllerValid;
	
	@FXML
	public CheckBox evolutionValid;
	
	@FXML
	protected Button graphClean;
	
	@FXML
	public Label simLabel;
	
	@FXML
	public Label controlLabel;
	
	@FXML
	public Accordion accordion;
	
	@FXML
	protected Label generationTag;
	
	@FXML
	protected StackPane SimComboBox;
	
	
	
}
