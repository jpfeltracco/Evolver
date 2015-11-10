package ui.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import controllers.Controller;
import evolver.ElementHolder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.stage.FileChooser;
import simulations.Simulation;
import ui.Builder.InputFramework;

public class FXController implements Initializable {
	ArrayList<Tab> EATabsArray = new ArrayList<Tab>();
	ArrayList<EATab> tabControllers = new ArrayList<EATab>();
	int eaCount = 0;

	
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
	public TabPane EATabs;

	@FXML
	private Tab addEAButton;
	
	//-----------------------------FXML Functions----------------------------
	
	@FXML
	private void addTab() {
		if (addEAButton.isSelected()) {
			addNewEATab();
		}
	}
	
	@FXML
	private void onSaveSettings(){
		for(EATab ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
				ea.saveAll(false);
				break;
			}
		}
	}
	
	@FXML
	private void saveEvolution(){
		for(EATab ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
				ea.saveAll(true);
				break;
			}
		}
	}
	
	@FXML
	private void onExportController(){
		for(EATab ea : tabControllers){
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
        	new FileChooser.ExtensionFilter("Evolve Setting", "*.evs"),
        	new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File selection = fileChooser.showOpenDialog(GUI.stage);
        boolean settingFile = selection.getName().endsWith("evs");
        
        int index = 0;
        
        SaveObject save = null;
	    Simulation sim = null;
	    Controller control = null;
	    InputFramework inputF = null;
	    ElementHolder elements = null;
	    byte[][] graphData = null;
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
			inputF = (InputFramework) in.readObject();
			in.close();
			fileIn.close();
		}catch(IOException i){
			i.printStackTrace();
			return;
		}catch(ClassNotFoundException c){
			System.out.println("Class not found.");
			c.printStackTrace();
			return;
		}
	    
	    addNewEATab(sim, control, inputF, elements, graphData);
	    
	}

	@FXML
	public void onClose(){
		GUI.stage.close();
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

	}

	public void addNewEATab(Simulation s, Controller c, InputFramework inputF, ElementHolder elements, byte[][] graphData) {
		
		Tab t = getNewEATab();
		
		//Add Tab Pane
		EATab tc = new EATab(t.getId(), t, this, s, c, inputF, elements, graphData);
		tabControllers.add(tc);
		
		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);
	
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("glass.accessible.force", "false"); 
		addNewEATab();
		EATabs.getSelectionModel().select(1);
		
		System.out.println(System.getProperty("os.name"));
		KeyCombination.Modifier metaDown;
		//KeyCombination.Modifier metaAny;
		if(System.getProperty("os.name").contains("Mac")){
			metaDown = KeyCombination.META_DOWN;
			//metaAny = KeyCombination.META_ANY;
		}else{
			metaDown = KeyCombination.CONTROL_DOWN;
			//metaAny = KeyCombination.CONTROL_ANY;
		}
		
		
		saveProject.setAccelerator(new KeyCodeCombination(KeyCode.S, metaDown));
		saveSettings.setAccelerator(new KeyCodeCombination(KeyCode.S, metaDown, KeyCombination.SHIFT_DOWN));
		openProject.setAccelerator(new KeyCodeCombination(KeyCode.O, metaDown));
		exportController.setAccelerator(new KeyCodeCombination(KeyCode.E, metaDown, KeyCombination.SHIFT_DOWN));
		closeButton.setAccelerator(new KeyCodeCombination(KeyCode.W, metaDown));
	}

}