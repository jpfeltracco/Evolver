package ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Vector;

import org.neuroph.util.TransferFunctionType;

import controllers.Controller;
import controllers.MLP;
import evolver.EvolutionAlgorithm;
import evolver.EvolutionAlgorithm.Type;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import simulations.Simulation;
import simulations.XOR;

public class FXController implements Initializable {
	boolean startClicked;

	// ArrayList<EvolutionAlgorithm> eas = new ArrayList<EvolutionAlgorithm>();
	ArrayList<Bridge> bridges = new ArrayList<Bridge>();
	ArrayList<Tab> EATabsArray = new ArrayList<Tab>();
	
	int eaCount = 0;

	@FXML
	private TabPane EATabs;

	// Tab that adds an EA to the list
	@FXML
	private Tab addEAButton;

	@FXML
	private void addTab() {
		if (addEAButton.isSelected()) {
			addEA();
		}
	}

	public void addEA() {
		boolean set = false;
		Tab t = null;
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
		EATabs.getTabs().add(t);
		EATabs.getTabs().sort(new TabOrder());
		bridges.add(new Bridge(t.getId(),this));
		
		t.setOnCloseRequest(new EventHandler<Event>() {
			@Override
			public void handle(Event t) {
				for(int i = 0; i < bridges.size(); i++){
					if(bridges.get(i).getTabID().equals(((Tab)t.getSource()).getId())){
						if(bridges.get(i).close()){
							EATabsArray.set(EATabsArray.indexOf(t.getSource()), null);
							bridges.remove(i);
						}else
							t.consume();
						break;
					}
				}
			}
		});

		

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

	//private FXMLLoader fXMLLoader = new FXMLLoader();

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

		EATabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				// System.out.println("Tab selected: " + newValue.getText());
				if (newValue == null)
					return;
				if (newValue.getContent() == null) {

					FXMLLoader fxmlLoader = new FXMLLoader();
					//fxmlLoader.setController(this);
					try {
						
						Parent root = (Parent) fxmlLoader.load(this.getClass().getResource("eaTab.fxml").openStream());
						
						newValue.setContent(root);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					// Content is already loaded. Update it if necessary.
					// Parent root = (Parent) newValue.getContent();
					// Optionally get the controller from Map and manipulate the
					// content
					// via its controller.
				}
			}
		});
		// By default, select 1st tab and load its content.
		EATabs.getSelectionModel().selectFirst();

	}

}