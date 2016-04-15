package ui.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import connection.ComManager;
import controllers.Controller;
//import de.codecentric.centerdevice.platform.osx.NSMenuBarAdapter;
//import de.codecentric.centerdevice.platform.osx.NativeMenuBar;
import evolver.ElementHolder;
import goals.Goal;
import goals.TestGoal;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import simulations.Simulation;
import ui.Builder.MenuItems;
import ui.Builder.TabListBuilder;
import ui.terminal.ConsoleRunner;
import ui.terminal.Interceptor;
//import osx.*;
import ui.terminal.Terminal;

public class FXController implements Initializable {
	ArrayList<Tab> EATabsArray = new ArrayList<Tab>();
	ArrayList<EATab> tabControllers = new ArrayList<EATab>();
	int eaCount = 0;
	boolean recentItemsExist = false;
	MenuItem clearRecent;
	MenuItem noRecentItems;
	SeparatorMenuItem recentSep;

	TabListBuilder tabBuilder;

	Terminal terminal;
	ConsoleRunner console;
	public static Interceptor out;

	//----------------------------FXML Objects----------------------------
	@FXML
	private MenuItem saveProject;

	@FXML
	private MenuItem saveSettings;

	@FXML
	private MenuItem openProject;

	@FXML
	private MenuItem exportController;

	@FXML
	private MenuItem closeButton;

	@FXML
	private MenuItem newProject;

	@FXML
	private MenuItem duplicateTab;

	@FXML
	private MenuItem saveAsMenu;

	@FXML
	private Menu openRecent;

	@FXML
	public TabPane EATabs;

	@FXML
	private Tab addEAButton;

	@FXML
    private MenuBar menuBar;

	@FXML
	protected ScrollPane tabArea;

	@FXML
	private AreaChart<Number,Number> memoryGraph;

	@FXML
	private AreaChart<Number,Number> cpuGraph;

	@FXML
	private AreaChart<Number,Number> threadGraph;

	@FXML
	private AreaChart<Number,Number> vTabGraph;

	//-------

	@FXML
	private TextField addressField;

	@FXML
	private TextField portField;

	@FXML
	private CheckBox acceptEvolutions;

	@FXML
	private CheckBox acceptBenchmarks;

	@FXML
	private Button connectButton;

	@FXML
	private ProgressBar serverStatusBar;

	@FXML
	private TextField serverStatusText;

	@FXML
	private TextArea consoleArea;

	@FXML
	private TextArea consoleOutput;

	@FXML
	private TextField consoleInput;

	//-----------------------------FXML Functions----------------------------

	@FXML
	private void onDuplicateTab(){

	}

	@FXML
	private void addTab() {
		if (addEAButton.isSelected()) {
			addNewEATab();
		}
	}

	@FXML
	private void saveGoal(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save VDriver Goal");
        fileChooser.setInitialDirectory(GUI.lastFileLocation);

        fileChooser.getExtensionFilters().addAll(
        	new FileChooser.ExtensionFilter("Evolve Goal", "*.egl"),
        	new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selection = fileChooser.showSaveDialog(GUI.stage);
        if(selection == null){
        	System.out.println("Save Goal Failed");
        	return;
        }

        GUI.lastFileLocation = new File(selection.getParent());
        GUI.lastFileName = selection.getName();



	}

	@FXML
	private void openAsVDriver(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open VDriver Project");
        fileChooser.setInitialDirectory(GUI.lastFileLocation);

        fileChooser.getExtensionFilters().addAll(
        	new FileChooser.ExtensionFilter("Evolve Files", new String[] {"*.evo", "*.evs"}),
           	new FileChooser.ExtensionFilter("Evolve Project", "*.evo"),
           	new FileChooser.ExtensionFilter("Evolve Setting", "*.evs"),
           	new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        List<File> selection = fileChooser.showOpenMultipleDialog(GUI.stage);
        if(selection == null){
        	System.out.println("Open Failed");
        	return;
        }
        GUI.lastFileLocation = new File(selection.get(0).getParent());
        GUI.lastFileName = selection.get(0).getName();

        fileChooser = new FileChooser();
		fileChooser.setTitle("Open VDriver Goal");
        fileChooser.setInitialDirectory(GUI.lastFileLocation);

        fileChooser.getExtensionFilters().addAll(
        	new FileChooser.ExtensionFilter("Evolve Goal", "*.egl"),
        	new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectionGoal = fileChooser.showOpenDialog(GUI.stage);
        if(selectionGoal == null){
        	System.out.println("Open Goal Failed");
        	return;
        }

        for(File f : selection){
        	openFile(f, selectionGoal);
        }
	}


	@FXML
	private void onSaveSettings(){
		for(EATab ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){

				final FileChooser fileChooser = new FileChooser();

				fileChooser.setTitle("Save Project");
				fileChooser.setInitialDirectory(GUI.lastFileLocation);

				String exts[] = new String[] {".evs"};
				if(GUI.lastFileName != null && GUI.lastFileName.length() > 0 && GUI.checkExt(GUI.lastFileName.substring(GUI.lastFileName.indexOf(".")),exts))
					fileChooser.setInitialFileName(GUI.lastFileName);

		        FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[2];
		        extensions[0] = new FileChooser.ExtensionFilter("Evolve Settings", "*.evs");
		        extensions[1] = new FileChooser.ExtensionFilter("All Files", "*.*");
		        fileChooser.getExtensionFilters().addAll(extensions);

		        File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);
		        if(selectedDirectory == null){
		        	System.out.println("Save Failed");
		        	return;
		        }
		        GUI.lastFileLocation = new File(selectedDirectory.getParent());
		        GUI.lastFileName = selectedDirectory.getName();
				if(ea.saveAll(false, selectedDirectory)){
					addRecentFile(selectedDirectory);
				}
				return;
			}
		}
	}

	private void saveAs(EATab ea){
		final FileChooser fileChooser = new FileChooser();


		fileChooser.setTitle("Save Project");
		fileChooser.setInitialDirectory(GUI.lastFileLocation);

		System.out.println(GUI.lastFileName);

		String exts[] = new String[] {".evo"};
		if(GUI.lastFileName != null && GUI.lastFileName.length() > 0 && GUI.checkExt(GUI.lastFileName.substring(GUI.lastFileName.indexOf(".")),exts))
			fileChooser.setInitialFileName(GUI.lastFileName);

        FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[2];
        extensions[0] = new FileChooser.ExtensionFilter("Evolve Project", "*.evo");
        extensions[1] = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extensions);


        File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);
        if(selectedDirectory == null){
        	System.out.println("Save Failed");
        	return;
        }
        GUI.lastFileLocation = new File(selectedDirectory.getParent());
        GUI.lastFileName = selectedDirectory.getName();
        System.out.println(GUI.lastFileName);

		if(ea.saveAll(true, selectedDirectory)){
			addRecentFile(selectedDirectory);
		}


	}

	@FXML
	private void saveAs(){
		for(EATab ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
				saveAs(ea);
				return;
			}
		}
	}


	@FXML
	private void saveEvolution(){
		for(EATab ea : tabControllers){
			if(ea.tabID.equals(EATabs.getSelectionModel().getSelectedItem().getId())){
				saveEvolution(ea);
				return;
			}
		}
		System.out.println("NO ID FOUND");
	}

	public void saveEvolution(EATab ea){
		System.out.println("Saving Evolution");
		System.out.println("\tSaved: " + ea.saved);
		if(ea.saved){
			ea.saveAll(true);
		}else{
			saveAs(ea);
		}
	}

	@FXML
	public void clearConsole(){
		out.clearTerminal();
	}

	@FXML
	public void saveConsole(){
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export System Console");
		fileChooser.setInitialDirectory(GUI.lastFileLocation);

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt"),
        		new FileChooser.ExtensionFilter("All Files", "*.*"));

	    File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);

	    try {
			PrintWriter out = new PrintWriter(selectedDirectory);
			out.print(FXController.out.getConsoleText());
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void saveOutput(){
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export User Console");
		fileChooser.setInitialDirectory(GUI.lastFileLocation);

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt"),
        		new FileChooser.ExtensionFilter("All Files", "*.*"));

	    File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);

	    try {
			PrintWriter out = new PrintWriter(selectedDirectory);
			out.print(FXController.out.getOutputText());
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@FXML
	private void onExportController(){
		for(EATab ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
				final FileChooser fileChooser = new FileChooser();
				Controller c = ea.ea.getBestElement();
				fileChooser.setTitle("Save Controller");
				fileChooser.setInitialDirectory(GUI.lastFileLocation);

		        String[] exts = c.getExtension();
		        FileChooser.ExtensionFilter[] extensions = new FileChooser.ExtensionFilter[exts.length+1];
		        for(int i = 0; i < exts.length; i++){
		        	extensions[i] = new FileChooser.ExtensionFilter(exts[i].toUpperCase(), "*." + exts[i]);
		        }
		        extensions[exts.length] = new FileChooser.ExtensionFilter("All Files", "*.*");
		        fileChooser.getExtensionFilters().addAll(extensions);

			    File selectedDirectory = fileChooser.showSaveDialog(GUI.stage);
				ea.exportController(selectedDirectory);
				break;
			}
		}
	}

	@FXML
	private void openEvolution(){
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Project");
        fileChooser.setInitialDirectory(GUI.lastFileLocation);

        fileChooser.getExtensionFilters().addAll(
        	new FileChooser.ExtensionFilter("Evolve Files", new String[] {"*.evo", "*.evs"}),
        	new FileChooser.ExtensionFilter("Evolve Project", "*.evo"),
        	new FileChooser.ExtensionFilter("Evolve Setting", "*.evs"),
        	new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        List<File> selection = fileChooser.showOpenMultipleDialog(GUI.stage);
        if(selection == null){
        	System.out.println("Open Failed");
        	return;
        }
        GUI.lastFileLocation = new File(selection.get(0).getParent());
        GUI.lastFileName = selection.get(0).getName();

        for(File f : selection){
        	openFile(f);
        }

	}

	public void openFile(File selection){
		String name = selection.getName().substring(0,selection.getName().indexOf("."));
		addRecentFile(selection);
		for(Tab t : EATabs.getTabs()){
			if(t.getId().indexOf("FILE") == -1)
				continue;
			System.out.println(t.getId().substring(5) + "\tvs.\t" + selection.getAbsolutePath());
			if(t.getId().substring(5).equals(selection.getAbsolutePath())){
				EATabs.getSelectionModel().select(t);
				return;
			}
		}

		boolean settingFile = selection.getName().endsWith("evs");

        int index = 0;

        SaveObject save = null;
	    Simulation sim = null;
	    Controller control = null;
	    MenuItems inputF = null;
	    ElementHolder elements = null;
	    byte[][][] graphData = null;
		try{
			FileInputStream fileIn = new FileInputStream(selection);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			save = (SaveObject) in.readObject();
			in.close();
			fileIn.close();

			ByteArrayInputStream byteIn = new ByteArrayInputStream(save.controller[index]);
			in = new ObjectInputStream(byteIn);
			control = (Controller) in.readObject();
			in.close();
			fileIn.close();

			byteIn = new ByteArrayInputStream(save.simulation[index]);
			in = new ObjectInputStream(byteIn);
			sim = (Simulation) in.readObject();
			in.close();
			fileIn.close();

			if(!settingFile){
				byteIn = new ByteArrayInputStream(save.elements[index]);
				in = new ObjectInputStream(byteIn);
				elements = (ElementHolder) in.readObject();
				in.close();
				fileIn.close();

				graphData = save.graph[index];
			}

			byteIn = new ByteArrayInputStream(save.evolve[index]);
			in = new ObjectInputStream(byteIn);
			inputF = (MenuItems) in.readObject();
			in.close();
			fileIn.close();
		}catch(IOException i){
			System.out.println("Open Failed: " + i.getMessage());
			return;
		}catch(ClassNotFoundException c){
			System.out.println("Class not found.");
			c.printStackTrace();
			return;
		}

		//if(!settingFile)
			addNewEATab(name, selection, sim, control, inputF, elements, graphData, selection);
		//else
			//new VDriver(sim, control, inputF, elements);

	}

	public void openFile(File selection, File goalLoc){
		String name = selection.getName().substring(0,selection.getName().indexOf("."));
		addRecentFile(selection);
		for(Tab t : EATabs.getTabs()){
			if(t.getId().indexOf("FILE") == -1)
				continue;
			System.out.println(t.getId().substring(5) + "\tvs.\t" + selection.getAbsolutePath());
			if(t.getId().substring(5).equals(selection.getAbsolutePath())){
				EATabs.getSelectionModel().select(t);
				return;
			}
		}

		boolean settingFile = selection.getName().endsWith("evs");

        int index = 0;

        SaveObject save = null;
	    Simulation sim = null;
	    Controller control = null;
	    MenuItems inputF = null;
	    ElementHolder elements = null;
	    byte[][][] graphData = null;
		try{
			FileInputStream fileIn = new FileInputStream(selection);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			save = (SaveObject) in.readObject();
			in.close();
			fileIn.close();

			ByteArrayInputStream byteIn = new ByteArrayInputStream(save.controller[index]);
			in = new ObjectInputStream(byteIn);
			control = (Controller) in.readObject();
			in.close();
			fileIn.close();

			byteIn = new ByteArrayInputStream(save.simulation[index]);
			in = new ObjectInputStream(byteIn);
			sim = (Simulation) in.readObject();
			in.close();
			fileIn.close();

			if(!settingFile){
				byteIn = new ByteArrayInputStream(save.elements[index]);
				in = new ObjectInputStream(byteIn);
				elements = (ElementHolder) in.readObject();
				in.close();
				fileIn.close();

				graphData = save.graph[index];
			}

			byteIn = new ByteArrayInputStream(save.evolve[index]);
			in = new ObjectInputStream(byteIn);
			inputF = (MenuItems) in.readObject();
			in.close();
			fileIn.close();
		}catch(IOException i){
			System.out.println("Open Failed: " + i.getMessage());
			return;
		}catch(ClassNotFoundException c){
			System.out.println("Class not found.");
			c.printStackTrace();
			return;
		}

		//if(!settingFile)
		new VDriver(sim, control, inputF, elements, new TestGoal());
		//	addNewEATab(name, selection, sim, control, inputF, elements, graphData);
		//else
			//new VDriver(sim, control, inputF, elements);
	}


	@FXML
	public void onClose(){
		Tab t = EATabs.getSelectionModel().getSelectedItem();
		if(t.getId().equals("SYSTEM")){
			GUI.running = false;
			Platform.exit();
		}
		for(EATab et: tabControllers){
			if(et.tab == t){
				closeTab(et);
				return;
			}
		}
		//GUI.stage.close();
	}

	public void closeTab(EATab t){
		if(!t.tab.getId().equals("SYSTEM") && !t.tab.getId().equals("NEW")){
			t.close();
			EATabs.getTabs().remove(t.tab);
		}
	}

	//----------------------------Normal Functions----------------------------

	public void addNewEATab() {
		Tab t = getNewEATab();

		//Add Tab Pane
		EATab tc = new EATab(t.getId(), t, this);
		tabControllers.add(tc);

		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);


		//System.out.println("Result: " + tabBuilder.addEATab(tc));
		tabArea.setContent(tabBuilder.addEATab(tc));

	}

	public void addNewEATab(String name, File file, Simulation s, Controller c, MenuItems inputF, ElementHolder elements, byte[][][] graphData, File loc) {

		Tab t = getNewEATab(name, file);

		//Add Tab Pane
		EATab tc = new EATab(t.getId(), t, this, s, c, inputF, elements, graphData, loc);
		tabControllers.add(tc);

		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);

		//System.out.println("Result: " + tabBuilder.addEATab(tc));
		tabArea.setContent(tabBuilder.addEATab(tc));

	}

	public void addRecentFile(File f){

		if(!recentItemsExist){
			openRecent.getItems().add(recentSep);
			openRecent.getItems().add(clearRecent);
			openRecent.getItems().remove(noRecentItems);
			recentItemsExist = true;
		}


		MenuItem newItem = GUI.addRecentFile(f);
		if(newItem == null){
			for(MenuItem m : openRecent.getItems()){
				if(m.getText().equals(f.getAbsolutePath())){
					openRecent.getItems().remove(m);
					openRecent.getItems().add(0, m);
					return;
				}
			}
			return;
		}

		newItem.setOnAction((event) -> {
			openFile(new File(newItem.getText()));
		});

		openRecent.getItems().add(0,newItem);
		String remove = GUI.getRemovedItem();
		if(remove == null)
			return;

		for(MenuItem m : openRecent.getItems()){
			if(m.getText().equals(remove)){
				openRecent.getItems().remove(m);
				return;
			}
		}
	}


	public void clearRecentItems(){
		openRecent.getItems().clear();
		GUI.recentFiles.clear();
		openRecent.getItems().add(noRecentItems);
		recentItemsExist = false;
	}

	public Tab getNewEATab(){
		//Set up Tab ID
		Tab t = null;
		boolean set = false;
		int tabID = 0;
		for (int i = 0; i < EATabsArray.size(); i++) {
			if (EATabsArray.get(i) == null) {
				t = new Tab("EA " + (i + 1));
				EATabsArray.set(i, t);
				set = true;
				tabID = i + 1;
				break;
			}
		}
		if (!set) {
			t = new Tab("EA " + (eaCount + 1));
			EATabsArray.add(t);
			tabID = eaCount + 1;
			eaCount++;
		}
		t.setId("" + tabID);
		return t;
	}

	public Tab getNewEATab(String name, File file){
		Tab t = new Tab(name);
		t.setId("FILE-" + file.getAbsolutePath());
		return t;
	}


	class TabOrder implements Comparator<Tab> {
		public int compare(Tab a, Tab b) {
			if (b.getText().equals("+"))
				return -1;
			else if (a.getText().equals("+"))
				return 1;
			else
				return 0;
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("glass.accessible.force", "false");

		out = new Interceptor(consoleArea, consoleOutput);
		terminal = new Terminal(consoleInput, out);
		ConsoleRunner console = new ConsoleRunner();

		Thread t = new Thread(console);
		t.setDaemon(true);
		t.start();

		tabBuilder = new TabListBuilder(this);
		//addNewEATab();
		//EATabs.getSelectionModel().select(1);


		System.out.println(System.getProperty("os.name") + "   Evolve " + GUI.VERSION + "   JavaFX " + System.getProperty("javafx.version"));



		KeyCombination.Modifier metaDown;
		//KeyCombination.Modifier metaAny;
		if(System.getProperty("os.name").contains("Mac")){
			metaDown = KeyCombination.META_DOWN;
			//metaAny = KeyCombination.META_ANY;
			menuBar.setUseSystemMenuBar(true);
			menuBar.useSystemMenuBarProperty().set(true);
		}else{
			metaDown = KeyCombination.CONTROL_DOWN;
			//metaAny = KeyCombination.CONTROL_ANY;
		}

		for(File f : GUI.recentFiles){
			MenuItem m = new MenuItem();
			m.setText(f.getAbsolutePath());
			m.setOnAction((event) -> {
				openFile(new File(m.getText()));
			});
			openRecent.getItems().add(0,m);
			recentItemsExist = true;
		}
		clearRecent = new MenuItem();
		clearRecent.setText("Clear Items");
		clearRecent.setOnAction((event) -> {
			clearRecentItems();
		});

		noRecentItems = new MenuItem();
		noRecentItems.setText("No Recent Items");
		noRecentItems.setDisable(true);
		recentSep = new SeparatorMenuItem();

		if(recentItemsExist){
			openRecent.getItems().add(recentSep);
			openRecent.getItems().add(clearRecent);
		}else{
			openRecent.getItems().add(noRecentItems);
		}


		new Thread(new ComManager(addressField, portField, acceptEvolutions, acceptBenchmarks
				, connectButton, serverStatusBar, serverStatusText)).start();


		saveProject.setAccelerator(new KeyCodeCombination(KeyCode.S, metaDown));
		saveSettings.setAccelerator(new KeyCodeCombination(KeyCode.E, metaDown, KeyCombination.SHIFT_DOWN));
		saveAsMenu.setAccelerator(new KeyCodeCombination(KeyCode.S, metaDown, KeyCombination.SHIFT_DOWN));
		openProject.setAccelerator(new KeyCodeCombination(KeyCode.O, metaDown));
		exportController.setAccelerator(new KeyCodeCombination(KeyCode.E, metaDown));
		closeButton.setAccelerator(new KeyCodeCombination(KeyCode.W, metaDown));
		newProject.setAccelerator(new KeyCodeCombination(KeyCode.N, metaDown));
		duplicateTab.setAccelerator(new KeyCodeCombination(KeyCode.D, metaDown));


		new Thread(new CPU(memoryGraph, cpuGraph, threadGraph, vTabGraph)).start();

	}

}
