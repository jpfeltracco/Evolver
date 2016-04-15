package ui.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.Arrays;
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
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import simulations.Renderable;
import simulations.Simulation;
import ui.Builder.Builder;
import ui.Builder.MenuItems;
import ui.Builder.TabMenu;
import ui.controllers.custom.TetheredButton;
import ui.controllers.custom.TetheredProgressBar;
//import ui.Builder.TetheredButton;
import ui.graph.DataBridge;
import ui.graph.DataBridge.ChartType;


public class EATab {
	
	String tabID;
	public final Tab tab;
	final FXController fxController;
	final String FXMLTYPE = "eaTab.fxml";
	public DataBridge fitnessGrapher;
	Builder builder = new Builder(this);
	EvolutionAlgorithm ea = new EvolutionAlgorithm();
	boolean original = true;
	Simulation simulation;
	Controller controller;
	ElementHolder elementHolder;
	boolean status[] = new boolean[3];
	VTab vTab;
	boolean rendering = false;
	File currentSaveLoc;
	String tabText;
	
	boolean saved = false;
	boolean changed = false;
	
	
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
		
		
		//vTab = new VTab(simulation, controller, ea, null, fitnessGrapher);
		
	}
	
	public EATab(String tabID, Tab tab, FXController fxController, Simulation simulation, Controller controller, MenuItems inputF, ElementHolder elements, byte[][][] graphData, File loc){
		currentSaveLoc = loc;
		saved = true;
		this.tabID = tabID;
		this.tab = tab;
		this.fxController = fxController;
		this.simulation = simulation;
		this.controller = controller;
		this.elementHolder = elements;
		ea.getMenuItems().clear();
		ea.menuInit();
		ea.getMenuItems().setDefaults(inputF);
		//ea.readElementHolder(elements);
		
		initializeTab();
		
		if(graphData != null && graphData.length != 0){
			byte[][] data = graphData[0];
			for(byte[] s : data){
				String sCurrentLine;
				BufferedReader br;
				StringTokenizer st;
				try {
					br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s)));
					sCurrentLine = br.readLine();
					String series = sCurrentLine;
					fitnessGrapher.addSeries(series, ChartType.FITNESS);
					sCurrentLine = br.readLine();
					while ((sCurrentLine = br.readLine()) != null) {
						st = new StringTokenizer(sCurrentLine, ",");
						fitnessGrapher.addToSeries(series, ChartType.FITNESS, new Number[] {Integer.parseInt(st.nextToken()) , Double.parseDouble(st.nextToken())});
					}
					br.close();
				} catch (IOException e) {
					throw new RuntimeException("There was an error opening the Elements: " + e);
				}
			}
			
			data = graphData[1];
			for(byte[] s : data){
				String sCurrentLine;
				BufferedReader br;
				StringTokenizer st;
				try {
					br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s)));
					sCurrentLine = br.readLine();
					String series = sCurrentLine;
					fitnessGrapher.addSeries(series, ChartType.ACTION);
					sCurrentLine = br.readLine();
					while ((sCurrentLine = br.readLine()) != null) {
						st = new StringTokenizer(sCurrentLine, ",");
						fitnessGrapher.addToSeries(series, ChartType.ACTION, new Number[] {Integer.parseInt(st.nextToken()) , Double.parseDouble(st.nextToken())});
					}
					br.close();
				} catch (IOException e) {
					throw new RuntimeException("There was an error opening the Elements: " + e);
				}
			}
				
		}
		

		GridPane grid = getNewGrid();
		evolutionScrollPane.setContent(builder.build(ea, grid));
		
		if(elements == null)
			fitnessGrapher.setGeneration(0);
		else
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
		
		
		//vTab = new VTab(simulation, controller, null, elements, fitnessGrapher);
		
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
	protected void onRenderClicked() {
		if (rendering) {
			rendering = false;
			renderButton.setText("Render");
			vTab.activate();
		} else {
			vTab.stop();
			renderButton.setText("Stop Render");
			stopped = true;
			rendering = true;
			
			elementHolder = ea.getExportedElements();
			Arrays.sort(elementHolder.getElements());
			
			Controller[] controllers = new Controller[vTab.simulation.getControlPerSim()];
			for (int i = 0; i < vTab.simulation.getControlPerSim(); i++) {
				controllers[i] = vTab.controller.clone();
				controllers[i].setConfig(elementHolder.getElements()[elementHolder.getElements().length - 1 - i].clone());
			}
			
			Simulation sim = vTab.simulation.clone();
			// TODO Make controllers accessible to Pong directly so we don't have to pass into render
			sim.setControllers(controllers);
			((Renderable) sim).render(controllers);
			
			
		}
	}
	
	@FXML
	protected void checkGraphClean(){
		if(fitnessGrapher != null && fitnessGrapher.getGraphSize("Fitness", ChartType.FITNESS)>4)
			fitnessGrapher.simplifyData("Fitness", ChartType.FITNESS, 2);
	}
	
	@FXML
	public void saveFitnessGraphData(){
		if(graphSelector.getSelectionModel().getSelectedIndex() == 0)
			saveChartAsXML(fitnessGraph);
		else if(graphSelector.getSelectionModel().getSelectedIndex() == 1)
			saveChartAsXML(actionGraph);
	}

	@FXML 
	protected void saveFitnessGraph(){
		if(graphSelector.getSelectionModel().getSelectedIndex() == 0)
			saveChartAsPng(fitnessGraph);
		else if(graphSelector.getSelectionModel().getSelectedIndex() == 1)
			saveChartAsPng(actionGraph);
	}
	
	@FXML
	protected void onClearClicked() {
		MenuItems def = ea.getMenuItems();
		
		fitnessGrapher.resetGraph(ChartType.FITNESS);
		fitnessGrapher.resetGraph(ChartType.ACTION);
		builder.setChangeable(true);
		elementHolder = null;
		ea = new EvolutionAlgorithm();
		ea.setDataBridge(fitnessGrapher);
		ea.menuInit();
		ea.getMenuItems().setDefaults(def);
		GridPane grid = getNewGrid();
		evolutionScrollPane.setContent(builder.build(ea, grid));
		clearButton.setDisable(true);
		fitnessGrapher.setProgress(0);
		
	}
	
	boolean stopped = true;
	boolean startClicked = false;
	@FXML
	protected void onStartClicked() {
		if (startClicked = !startClicked) {
			stopped = false;
			changed = true;
			clearButton.setDisable(true);
			graphClean.setDisable(true);
			builder.setChangeable(false);
			renderButton.setDisable(false);
			//System.out.println("STARTING SIMULATION");
			//System.out.println("SIM NUM IN: " + simulation.getNumInputs() + "\tNUM OUT: " + simulation.getNumOutputs());
			//controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
			//if(simulation instanceof TabMenu)
				//((TabMenu)simulation).start();
			//if(controller instanceof TabMenu)
				//((TabMenu)controller).start();
			//ea.readElementHolder(elementHolder);
			//ea.setSimAndController(simulation,controller);
			//((TabMenu)ea).start();
			//ea.setRunning(true);
			//(new Thread(ea)).start();
			
			//vTab.updateComponents(simulation, controller, ea);
			//vTab.re
			vTab = new VTab(simulation, controller, ea, null, elementHolder, fitnessGrapher);
			vTab.activate();
			changed = true;
			updateTabText();
			startButton.setText("Stop");
			
			
		} else {
			stopped = true;
			renderButton.setDisable(true);
			ea.setRunning(false);
			clearButton.setDisable(false);
			graphClean.setDisable(false);
			//builder.setChangable(true);
			elementHolder = ea.getExportedElements();
			
			vTab.stop();
			
			startButton.setText("Start");
		}
	}
	
	@FXML
	void toggleGraphAnimate() {
		fitnessGraph.setAnimated(!fitnessGraph.animatedProperty().get());
		actionGraph.setAnimated(!actionGraph.animatedProperty().get());
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
					if(!tab.getId().contains("FILE"))
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
		
		/*final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generation");
        yAxis.setLabel("Fitness");
	    fitnessGraph = new LineChart<Number,Number>(xAxis,yAxis);
	    fitnessGraph.setTitle(tab.getText() + " Fitness Graph");*/
		
		//Set up grapher
		System.out.println("GRAPH: " + fitnessGraph);
		fitnessGrapher = new DataBridge(fitnessGraph, actionGraph, this);
		//ea.setGrapher(fitnessGrapher);
		
		//Set the initial button enables
		startButton.setTetheredDisable(true);
		clearButton.setDisable(true);
		graphClean.setDisable(false);
		renderButton.setDisable(true);
			
		//Sets Non-changeable items
		builder.addNonChangable(simType);
		builder.addNonChangable(controllerType);
			
		//Sets the Evolution Section
		accordion.setExpandedPane(evolutionPane);
		
		//Set the Simulation and Controller drop downs
		simType.getItems().addAll(Simulation.getTypeOfSimulations());
		controllerType.getItems().addAll(Controller.getTypeOfControllers());
		
		progressBar.setLabel(progressLabel);
		
		accordion.setMinWidth(229);
		setTabText(tab.getText(),false);
	}
	
	public String getTabID(){
		return tabID;
	}
	
	public boolean saveAll(boolean saveElement){
		return saveAll(saveElement, currentSaveLoc);
	}
	
	public boolean saveAll(boolean saveElement, File file){
		return saveAll(saveElement, file, new SaveObject(), 0);
	}
	
	public boolean saveAll(boolean saveElements, File selectedDirectory, SaveObject output, int index){
		ElementHolder elementHolder = null;
		if(saveElements)
			 elementHolder = getElementHolder();
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
			return false;
		}
	    String name = selectedDirectory.getName().substring(0,selectedDirectory.getName().indexOf("."));
	    setTabText(name, false);
	    
	    if(!tab.getId().contains("FILE")){
	    	fxController.EATabsArray.set(fxController.EATabsArray.indexOf(tab), null);
	    }
	    tab.setId("FILE-" + selectedDirectory.getAbsolutePath());
	    tabID = "FILE-" + selectedDirectory.getAbsolutePath();
	    saved = true;
	    changed = false;
	    updateTabText();
	    currentSaveLoc = selectedDirectory;
	    return true;
	    
	}
	
	public void setChanged(boolean val){
		if(changed != val && original){
			changed = val;
			setTabText(tabText);
			updateName();
		}	
	}
	
	public void setTabText(String name, boolean val){
		if(original)
			changed = val;
		setTabText(name);
		updateName();
	}
	
	public void setTabText(String name){
		tabText = name;
		if(!original)
			return;
		if(changed)
			tab.setText("*"+tabText);
		else
			tab.setText(tabText);
		updateName();
	}
	
	public void updateTabText(){
		if(changed)
			tab.setText("*"+tabText);
		else
			tab.setText(tabText);
		//updateName();
	}
	
	private void updateName(){
		fxController.tabArea.setContent(fxController.tabBuilder.build());
	}
	
	public String getTabText(){
		return tabText;
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
		startButton.setTetheredDisable(!ready);
	}
	
	protected GridPane getNewGrid(){
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(2);
		
		//grid.setGridLinesVisible(true);
		
		ColumnConstraints c = new ColumnConstraints();
		//c.setHgrow(Priority.SOMETIMES);
		c.setMaxWidth(100.0);
		c.setMinWidth(100.0);
		grid.getColumnConstraints().add(c);
		
		c = new ColumnConstraints();
		c.setHalignment(HPos.RIGHT);
		c.setHgrow(Priority.ALWAYS);
		//c.setMinWidth(200);
		grid.getColumnConstraints().add(c);
		
		RowConstraints r = new RowConstraints();
		r.setVgrow(Priority.NEVER);
		grid.getRowConstraints().add(r);
		
		return grid;
	}
	
	public void setGeneration(int g){
		generationTag.setText("" + g);
	}
	
	public void setFitness(double d){
		fitnessTag.setText("" + (Math.round(d*100.0)/100.0) );
	}
	
	public void saveChartAsPng(LineChart<Number,Number> graph) {
		// TODO: Review this process
		
		
		
		/*final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generation");
        yAxis.setLabel("Fitness");
        LineChart<Number,Number> outGraph = new LineChart<Number,Number>(xAxis,yAxis);
        outGraph.setTitle(tab.getText() + " Fitness Graph");
	    
	    outGraph.setData(graph.getData());
	    outGraph.setMinWidth(720);
	    outGraph.setMinHeight(720);*/
		
		
	    WritableImage image = graph.snapshot(new SnapshotParameters(), null);
	    
	    
	    final FileChooser fileChooser = new FileChooser();
		
		fileChooser.setTitle("Save Graph Image");
		fileChooser.setInitialDirectory(GUI.lastFileLocation);  
		
		String exts[] = new String[] {".png"};
		if(GUI.lastFileName != null && GUI.lastFileName.length() > 0 && GUI.checkExt(GUI.lastFileName.substring(GUI.lastFileName.indexOf(".")),exts))
			fileChooser.setInitialFileName(GUI.lastFileName);
		
        FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[2];
        extensions[0] = new FileChooser.ExtensionFilter("PNG Image", "*.png");
        extensions[1] = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extensions);
        
        File file = fileChooser.showSaveDialog(GUI.stage);

	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	    } catch (IOException e) {
	        // TODO: handle exception here
	    }
	}
	
	public void saveChartAsXML(Chart graph) {
		// TODO: Review this process
		
		final FileChooser fileChooser = new FileChooser();
			
		fileChooser.setTitle("Save Graph Data");
		fileChooser.setInitialDirectory(GUI.lastFileLocation);  
		
		String exts[] = new String[] {".txt"};
		if(GUI.lastFileName != null && GUI.lastFileName.length() > 0 && GUI.checkExt(GUI.lastFileName.substring(GUI.lastFileName.indexOf(".")),exts))
			fileChooser.setInitialFileName(GUI.lastFileName);
		
		FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[2];
		extensions[0] = new FileChooser.ExtensionFilter("TEXT File", "*.txt");
		extensions[1] = new FileChooser.ExtensionFilter("All Files", "*.*");
		fileChooser.getExtensionFilters().addAll(extensions);
		
		File file = fileChooser.showSaveDialog(GUI.stage);
	    
	    try {
	    	if(graph.getTitle().equals("Fitness"))
	    		fitnessGrapher.writeData(file, ChartType.FITNESS);
	    	else if(graph.getTitle().equals("Actions"))
	    		fitnessGrapher.writeData(file, ChartType.ACTION);
		} catch (IOException e) {
			// TODO: handle exception here
		}
	    
	}
	
	public synchronized void exportController(File selectedDirectory){
		// TODO: Make this save the last place something was saved, and then set the initial directory to there
		Controller c = ea.getBestElement();
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }else{
			c.saveConfig(selectedDirectory);
	    }
	}
	

	public boolean close(){

		fxController.tabArea.setContent(fxController.tabBuilder.removeEATab(this));
		
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

		setChanged(true);

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
        
        setChanged(true);
        
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
	public TetheredButton startButton;
	
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
	protected LineChart<Number, Number> actionGraph;
	
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
	protected Label fitnessTag;
	
	@FXML
	protected StackPane SimComboBox;
	
	@FXML
	public TetheredProgressBar progressBar;
	
	@FXML
	public Label progressLabel;
	
	@FXML
	public TabPane graphSelector;
	
	
	
}
