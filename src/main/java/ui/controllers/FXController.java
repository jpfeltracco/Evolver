package ui.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import controllers.Controller;
//import de.codecentric.centerdevice.platform.osx.NSMenuBarAdapter;
//import de.codecentric.centerdevice.platform.osx.NativeMenuBar;
import evolver.ElementHolder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import simulations.Simulation;
import ui.Builder.MenuItems;
//import osx.*;

public class FXController implements Initializable {
	ArrayList<Tab> EATabsArray = new ArrayList<Tab>();
	ArrayList<EATab> tabControllers = new ArrayList<EATab>();
	int eaCount = 0;
	boolean recentItemsExist = false;
	MenuItem clearRecent;
	MenuItem noRecentItems;
	SeparatorMenuItem recentSep;
	
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
	private Menu openRecent;
	
	@FXML
	public TabPane EATabs;

	@FXML
	private Tab addEAButton;
	
	@FXML
    private MenuBar menuBar;
	
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
	
	@FXML
	private void saveEvolution(){
		for(EATab ea : tabControllers){
			if(ea.tabID == EATabs.getSelectionModel().getSelectedItem().getId()){
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
				return;
			}
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
			addNewEATab(name, selection, sim, control, inputF, elements, graphData);
		//else
			//new VDriver(sim, control, inputF, elements);
			
	}

	@FXML
	public void onClose(){
		Tab t = EATabs.getSelectionModel().getSelectedItem();
		if(!t.getId().equals("SYSTEM") && !t.getId().equals("NEW") )
			EATabs.getTabs().remove(t);
		//GUI.stage.close();
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

	public void addNewEATab(String name, File file, Simulation s, Controller c, MenuItems inputF, ElementHolder elements, byte[][] graphData) {
		
		Tab t = getNewEATab(name, file);
		
		//Add Tab Pane
		EATab tc = new EATab(t.getId(), t, this, s, c, inputF, elements, graphData);
		tabControllers.add(tc);
		
		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);
	
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
		addNewEATab();
		EATabs.getSelectionModel().select(1);
		
		
		System.out.println(System.getProperty("os.name"));
		
		
		
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
		
		
		/*NativeMenuBar adapter = new NSMenuBarAdapter();

		MenuBar menuBar = adapter.getMenuBar();
		menuBar.getMenus().get(0).setText("Hello World");
		menuBar.getMenus().get(0).getItems().get(0).setText("Yeeha");
		adapter.setMenuBar(menuBar);*/
		
		
		saveProject.setAccelerator(new KeyCodeCombination(KeyCode.S, metaDown));
		saveSettings.setAccelerator(new KeyCodeCombination(KeyCode.S, metaDown, KeyCombination.SHIFT_DOWN));
		openProject.setAccelerator(new KeyCodeCombination(KeyCode.O, metaDown));
		exportController.setAccelerator(new KeyCodeCombination(KeyCode.E, metaDown));
		closeButton.setAccelerator(new KeyCodeCombination(KeyCode.W, metaDown));
		newProject.setAccelerator(new KeyCodeCombination(KeyCode.N, metaDown));
		duplicateTab.setAccelerator(new KeyCodeCombination(KeyCode.D, metaDown));
	}

}