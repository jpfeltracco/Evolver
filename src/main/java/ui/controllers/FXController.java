package ui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class FXController implements Initializable {
	boolean startClicked;

	// ArrayList<EvolutionAlgorithm> eas = new ArrayList<EvolutionAlgorithm>();
	ArrayList<Tab> EATabsArray = new ArrayList<Tab>();
	ArrayList<TabController> tabControllers = new ArrayList<TabController>();
	
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

	public void addNewEATab() {
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
		
		//Add Tab Pane
		TabController tc = new TabController(t.getId(), this, t);
		tabControllers.add(tc);
		
		//Show Tab
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		EATabs.getSelectionModel().select(t);

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
		System.setProperty("glass.accessible.force", "false"); // Windows 10
																// crashes
																// unless this
																// is here :(

		// Demo of way to populate combo box programmatically
		// ObservableList<String> options =
		// FXCollections.observableArrayList(
		// "MLP 3x3",
		// "FPGA",
		// "Norris's brain"
		// );
		// controllerCombo.setItems(options);
		//
		// // Demo of way to populate graph programmatically
		// series = new Series<Number, Number>();
		// //populating the series with data
		// series.getData().add(new XYChart.Data<Number, Number>(1, 23));
		// series.getData().add(new XYChart.Data<Number, Number>(2, 14));
		// series.getData().add(new XYChart.Data<Number, Number>(3, 15));
		// series.getData().add(new XYChart.Data<Number, Number>(4, 24));
		// series.getData().add(new XYChart.Data<Number, Number>(5, 34));
		// series.getData().add(new XYChart.Data<Number, Number>(6, 36));
		// series.getData().add(new XYChart.Data<Number, Number>(7, 22));
		// series.getData().add(new XYChart.Data<Number, Number>(8, 45));
		// series.getData().add(new XYChart.Data<Number, Number>(9, 43));
		// series.getData().add(new XYChart.Data<Number, Number>(10, 17));
		// series.getData().add(new XYChart.Data<Number, Number>(11, 29));
		// series.getData().add(new XYChart.Data<Number, Number>(12, 25));
		//
		// EATabs.getSelectionModel().clearSelection();

		
		
		
		// By default, select 1st tab and load its content.
		EATabs.getSelectionModel().selectFirst();

	}

}