package ui.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import controllers.Controller;
import evolver.ElementHolder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import simulations.Simulation;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;

public class FXController implements Initializable {
	boolean startClicked;

	// ArrayList<EvolutionAlgorithm> eas = new ArrayList<EvolutionAlgorithm>();
	ArrayList<Tab> EATabsArray = new ArrayList<Tab>();
	ArrayList<EATabHolder> tabControllers = new ArrayList<EATabHolder>();
	
	int eaCount = 0;

	@FXML
	public TabPane EATabs;

	// Tab that adds an EA to the list
	@FXML
	private Tab addEAButton;

	@FXML
	private void addTab() {
		if (addEAButton.isSelected()) {
			addNewEATab();
		}
	}
	
	@FXML
	private void saveEvolution(){
		for(EATabHolder ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
				ea.saveAll();
				break;
			}
		}
	}
	
	@FXML
	private void onExportController(){
		for(EATabHolder ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
				ea.exportController();
				break;
			}
		}
	}
	
	@FXML
	private void openEvolution(){
		final FileChooser fileChooser = new FileChooser();
		
		fileChooser.setTitle("Open Project");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );   
        
        fileChooser.getExtensionFilters().addAll(
        	new FileChooser.ExtensionFilter("Evolve Project", "*.evo"),
        	new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File selection = fileChooser.showOpenDialog(GUI.stage);
	    
        System.out.println(selection.getAbsolutePath());
        
        
        
        SaveObject save = null;
        
	    Simulation sim = null;
	    Controller control = null;
	    InputFramework inputF = null;
	    ElementHolder elements = null;
	    String[] graphData = null;
		try{
			FileInputStream fileIn = new FileInputStream(selection);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			save = (SaveObject) in.readObject();
			in.close();
			fileIn.close();
			//---
			
			ByteArrayInputStream byteIn = new ByteArrayInputStream(save.controller);
			in = new ObjectInputStream(byteIn);
			control = (Controller) in.readObject();
			in.close();
			fileIn.close();
			
			byteIn = new ByteArrayInputStream(save.simulation);
			in = new ObjectInputStream(byteIn);
			sim = (Simulation) in.readObject();
			in.close();
			fileIn.close();
			
			byteIn = new ByteArrayInputStream(save.elements);
			in = new ObjectInputStream(byteIn);
			elements = (ElementHolder) in.readObject();
			in.close();
			fileIn.close();
			
			byteIn = new ByteArrayInputStream(save.evolve);
			in = new ObjectInputStream(byteIn);
			inputF = (InputFramework) in.readObject();
			in.close();
			fileIn.close();
			
			graphData = new String[save.graph.length];
			for(int i = 0; i < save.graph.length; i++){
				graphData[i] = new String(save.graph[i]);
			}
			
			
			
			/*FileInputStream fileIn = new FileInputStream(dir + "simulation.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			sim = (Simulation) in.readObject();
			in.close();
			fileIn.close();
			
			fileIn = new FileInputStream(dir + "controller.ser");
			in = new ObjectInputStream(fileIn);
			control = (Controller) in.readObject();
			in.close();
			fileIn.close();
			
			fileIn = new FileInputStream(dir + "evolve.ser");
			in = new ObjectInputStream(fileIn);
			inputF = (InputFramework) in.readObject();
			in.close();
			fileIn.close();
			
			fileIn = new FileInputStream(dir + "elements.ser");
			in = new ObjectInputStream(fileIn);
			elements = (ElementHolder) in.readObject();
			in.close();
			fileIn.close();*/
		}catch(IOException i){
			i.printStackTrace();
			return;
		}catch(ClassNotFoundException c){
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}
		
		
	    
	    
	    System.out.println("Simulation HasMenu: " + (sim instanceof HasMenu));
	    //System.out.println(((HasMenu)sim).getFramework().getVariable(0).getRawVariable());
	    
	    EATabController ea = addNewEATab(sim, control, inputF, elements, graphData);
	    //ea.setSimulation(sim);
	    //ea.setController(control);
	    
	}

	public EATabController addNewEATab() {
		
		Tab t = getNewEATab();
		
		//Add Tab Pane
		EATabController tc = new EATabController(t.getId(), this, t);
		tabControllers.add(tc);
		
		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);
		
		return tc;

	}

	public EATabController addNewEATab(Simulation s, Controller c, InputFramework inputF, ElementHolder elements, String[] graphData) {
		
		Tab t = getNewEATab();
		
		//Add Tab Pane
		EATabController tc = new EATabController(t.getId(), this, t, s, c, inputF, elements, graphData);
		tabControllers.add(tc);
		
		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);
		
		return tc;
	
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
	
	@FXML
	private MenuItem saveProject;
	
	@FXML
	private MenuItem openProject;
	
	@FXML
	private MenuItem exportController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("glass.accessible.force", "false"); 
		addNewEATab();
		EATabs.getSelectionModel().select(1);
		
		System.out.println(System.getProperty("os.name"));
		if(System.getProperty("os.name").contains("Mac")){
			saveProject.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
			openProject.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
			exportController.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN));
		}else{
			saveProject.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
			openProject.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
			exportController.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		}
	}

}