package ui.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import controllers.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
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
	private void openEvolution(){
		final DirectoryChooser directoryChooser = new DirectoryChooser();
	    File selectedDirectory = directoryChooser.showDialog(GUI.stage);
	    if (selectedDirectory == null) {
	    	throw new RuntimeException("Directory error.");
	    }
	    
	    String dir = selectedDirectory.getAbsolutePath() + "/";
	    
	    Simulation sim = null;
	    Controller control = null;
	    InputFramework inputF = null;
		try{
			FileInputStream fileIn = new FileInputStream(dir + "simulation.ser");
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
	    
	    EATabController ea = addNewEATab(sim, control, inputF);
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

	public EATabController addNewEATab(Simulation s, Controller c, InputFramework inputF) {
		
		Tab t = getNewEATab();
		
		//Add Tab Pane
		EATabController tc = new EATabController(t.getId(), this, t, s, c, inputF);
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("glass.accessible.force", "false"); 
		addNewEATab();
		EATabs.getSelectionModel().select(1);

	}

}