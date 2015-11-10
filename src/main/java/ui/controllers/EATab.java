package ui.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
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
import ui.Builder.TabMenu;
import ui.Builder.MenuItems;
import ui.graph.Graph;

public class EATab {
	
	final String tabID;
	final Tab tab;
	final FXController fxController;
	final String FXMLTYPE = "eaTab.fxml";
	public Graph fitnessGrapher;
	Builder builder = new Builder(this);
	EvolutionAlgorithm ea = new EvolutionAlgorithm();
	boolean original = true;
	Simulation simulation;
	Controller controller;
	ElementHolder elementHolder;
	boolean status[] = new boolean[3];
	
	
	public EATab(String tabID, Tab tab, FXController fxController){
		this.tabID = tabID;
		this.tab = tab;
		this.fxController = fxController;
		
		this.simulation = Simulation.getSimulation(Simulation.getTypeOfSimulations()[0]);
		this.controller = Controller.getController(Controller.getTypeOfControllers()[0]);
		
		initializeTab();
		setEvolutionSection();
		
		//------
		
		original = false;
		simType.getSelectionModel().select(Simulation.getTypeOfSimulations()[0]);
		controllerType.getSelectionModel().select(Controller.getTypeOfControllers()[0]);
		changeSim(simulation);
		changeController(controller);
		original = true;
		
		
		
	}
	
	public EATab(String tabID, Tab tab, FXController fxController, Simulation simulation, Controller controller, MenuItems inputF, ElementHolder elements, byte[][] graphData){
		this.tabID = tabID;
		this.tab = tab;
		this.fxController = fxController;
		this.simulation = simulation;
		this.controller = controller;
		
		ea.getMenuItems().clear();
		ea.menuInit();
		ea.getMenuItems().setDefaults(inputF);
		ea.readElementHolder(elements);
		
		initializeTab();
		
		if(graphData != null && graphData.length != 0){
			for(byte[] s : graphData){
				String sCurrentLine;
				BufferedReader br;
				StringTokenizer st;
				try {
					br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s)));
					sCurrentLine = br.readLine();
					String series = sCurrentLine;
					fitnessGrapher.addSeries(series);
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
		
		fitnessGrapher.setGeneration(elements.getGen());
		
		//------
		
		String simClassName = simulation.getClass().getName();
		String simName = simClassName.substring(simClassName.indexOf(".") + 1);
		String controllerClassName = controller.getClass().getName();
		String controllerName = controllerClassName.substring(controllerClassName.indexOf(".") + 1);
		
		if(!Simulation.checkExists(simName)){
			simType.getItems().add(simName);
		}
		
		if(!Controller.checkExists(controllerName)){
			controllerType.getItems().add(controllerName);
		}
		
		original = false;
		simType.getSelectionModel().select(simName);
		controllerType.getSelectionModel().select(controllerName);
		changeSim(simulation);
		changeController(controller);
		original = true;
		
	}
	
	//-----------------------------FXML Functions----------------------------
	
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

	@FXML 
	protected void saveFitnessGraph(){
		saveChartAsPng(fitnessGraph);
	}
	
	@FXML
	protected void onClearClicked() {
		MenuItems def = ea.getMenuItems();
		
		fitnessGrapher.resetGraph();
		builder.setChangable(true);
		elementHolder = null;
		ea = new EvolutionAlgorithm();
		ea.menuInit();
		ea.getMenuItems().setDefaults(def);
		ea.setGrapher(fitnessGrapher);
		GridPane grid = getNewGrid();
		evolutionScrollPane.setContent(builder.build(ea, grid));
		clearButton.setDisable(true);
		
	}
	
	boolean stopped = true;
	boolean startClicked = false;
	@FXML
	protected void onStartClicked() {
		if (startClicked = !startClicked) {
			stopped = false;
			
			clearButton.setDisable(true);
			graphClean.setDisable(true);
			builder.setChangable(false);
			//System.out.println("STARTING SIMULATION");
			//System.out.println("SIM NUM IN: " + simulation.getNumInputs() + "\tNUM OUT: " + simulation.getNumOutputs());
			controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
			if(simulation instanceof TabMenu)
				((TabMenu)simulation).start();
			if(controller instanceof TabMenu)
				((TabMenu)controller).start();
			ea.readElementHolder(elementHolder);
			ea.setSimAndController(simulation,controller);
			((TabMenu)ea).start();
			ea.setRunning(true);
			(new Thread(ea)).start();
			startButton.setText("Stop");
		} else {
			stopped = true;
			ea.setRunning(false);
			clearButton.setDisable(false);
			graphClean.setDisable(false);
			//builder.setChangable(true);
			elementHolder = ea.getExportedElements();
			startButton.setText("Start");
		}
	}
	
	//----------------------------Normal Functions-----------------------------
	
	public void setEvolutionSection(){
		GridPane grid = getNewGrid();
		ea.menuInit();
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
		System.out.println("GRAPH: " + fitnessGraph);
		fitnessGrapher = new Graph(fitnessGraph, this);
		ea.setGrapher(fitnessGrapher);
		
		//Set the initial button enables
		startButton.setDisable(true);
		clearButton.setDisable(true);
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
	
	public void saveAll(boolean saveElement){
		saveAll(saveElement, new SaveObject(), 0);
	}
	
	public void saveAll(boolean saveElements, SaveObject output, int index){
		ElementHolder elementHolder = null;
		if(saveElements)
			 elementHolder = getElementHolder();
		
		final FileChooser fileChooser = new FileChooser();
		
		fileChooser.setTitle("Save Project");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );   
        
        FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[2];
        if(saveElements)
        	extensions[0] = new FileChooser.ExtensionFilter("Evolve Project", "*.evo");
        else
        	extensions[0] = new FileChooser.ExtensionFilter("Evolve Settings", "*.evs");
        extensions[1] = new FileChooser.ExtensionFilter("All Files", "*.*");
    	
        fileChooser.getExtensionFilters().addAll(extensions);
        
        File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);
        
	    try {
	    	if(saveElements)
	    		output.graph[index] = fitnessGrapher.streamData();
			
	    	ByteArrayOutputStream controllerByteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(controllerByteOut);
			out.writeObject(controller);
			out.close();
			output.controller[index] = controllerByteOut.toByteArray();
			
			ByteArrayOutputStream simulationByteOut = new ByteArrayOutputStream();
			out = new ObjectOutputStream(simulationByteOut);
			out.writeObject(simulation);
			out.close();
			output.simulation[index] = simulationByteOut.toByteArray();
			
			if(saveElements){
				ByteArrayOutputStream elementsByteOut = new ByteArrayOutputStream();
				out = new ObjectOutputStream(elementsByteOut);
				out.writeObject(elementHolder);
				out.close();
				output.elements[index] = elementsByteOut.toByteArray();
			}
			
			ByteArrayOutputStream evolveByteOut = new ByteArrayOutputStream();
			out = new ObjectOutputStream(evolveByteOut);
			out.writeObject(ea.getMenuItems());
			out.close();
			output.evolve[index] = evolveByteOut.toByteArray();
			
			ByteArrayOutputStream saveObject = new ByteArrayOutputStream();
			out = new ObjectOutputStream(saveObject);
			out.writeObject(output);
			out.close();
			
			FileOutputStream fos = new FileOutputStream(selectedDirectory);
			fos.write(saveObject.toByteArray());
			fos.close();

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
	
	public synchronized void exportController(){
		// TODO: Make this save the last place something was saved, and then set the initial directory to there
		
		final FileChooser fileChooser = new FileChooser();
		Controller c = ea.getBestElement();
		
		fileChooser.setTitle("Save Controller");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );   
        
        String[] exts = c.getExtension();
        FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[exts.length+1];
        for(int i = 0; i < exts.length; i++){
        	extensions[i] = new FileChooser.ExtensionFilter(exts[i].toUpperCase(), "*." + exts[i]);
        }
        extensions[exts.length] = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extensions);
        
	    File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }else{
			c.saveConfig(selectedDirectory);
	    }
	}
	
	public boolean close(){
		System.out.println("Closing tabID: " + tabID);
		ea.setRunning(false);
		return true;
	}
	
	public ElementHolder getElementHolder(){
		return elementHolder;
	}
	
	protected void changeSim(Simulation sim) {
		String val = (String)simType.getValue();
		
		System.out.println("Making Sim: " + sim);
		

		simLabel.setText(val);
		simType.getSelectionModel().select(val);
		GridPane grid = getNewGrid();
        grid.add(simType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        if(sim instanceof TabMenu){
        	((TabMenu)sim).getMenuItems().clear();
        	((TabMenu)sim).menuInit();
        	System.out.println("Building Simulation: " + sim);
        	simScrollPane.setContent(builder.build(sim, grid));
        }else{
        	simulationValid.setSelected(true);
        	status[0] = true;
        	simScrollPane.setContent(grid);
        	checkValidity();
        }
		simPane.requestFocus();

	}
	
	protected void changeController(Controller control){
		String val = (String)controllerType.getValue();
		
		controlLabel.setText(val);
		
		GridPane grid = getNewGrid();
		
        grid.add(controllerType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        
        if(control instanceof TabMenu){
        	((TabMenu)control).getMenuItems().clear();
        	((TabMenu)control).menuInit();
        	System.out.println("Building Controller: " + control);
        	controllerScrollPane.setContent(builder.build(control, grid));
        }else{
        	controllerValid.setSelected(true);
        	status[1] = true;
        	controllerScrollPane.setContent(grid);
        	checkValidity();
        }
        
        controllerPane.requestFocus();
	}
	
	//----------------------------FXML Objects----------------------------
	
	@FXML
	protected Button startButton;
	
	@FXML
	protected Button clearButton;
	
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