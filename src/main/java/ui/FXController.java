package ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class FXController implements Initializable {
	
	// Yo Keenan! These are populated by FXML voodoo magic from the FXMLLoader in Main!
    @FXML
    private Button start_button;

    @FXML
    private Button render_button;

    @FXML
    private Slider mutRateSlider;

    @FXML
    private Slider mutAmtSlider;

    @FXML
    private ComboBox<String> controllerCombo;

    @FXML
    private LineChart<Number, Number> fitnessGraph;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("glass.accessible.force", "false"); // Windows 10 crashes unless this is here :(
		
		// Demo of way to populate combo box programmatically
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "MLP 3x3",
			        "FPGA",
			        "Norris's brain"
			    );
		controllerCombo.setItems(options);
		
		// Demo of way to populate graph programmatically
		XYChart.Series<Number, Number> series = new Series<Number, Number>();
        //populating the series with data
        series.getData().add(new XYChart.Data<Number, Number>(1, 23));
        series.getData().add(new XYChart.Data<Number, Number>(2, 14));
        series.getData().add(new XYChart.Data<Number, Number>(3, 15));
        series.getData().add(new XYChart.Data<Number, Number>(4, 24));
        series.getData().add(new XYChart.Data<Number, Number>(5, 34));
        series.getData().add(new XYChart.Data<Number, Number>(6, 36));
        series.getData().add(new XYChart.Data<Number, Number>(7, 22));
        series.getData().add(new XYChart.Data<Number, Number>(8, 45));
        series.getData().add(new XYChart.Data<Number, Number>(9, 43));
        series.getData().add(new XYChart.Data<Number, Number>(10, 17));
        series.getData().add(new XYChart.Data<Number, Number>(11, 29));
        series.getData().add(new XYChart.Data<Number, Number>(12, 25));
        
        fitnessGraph.getData().add(series);
	}

}