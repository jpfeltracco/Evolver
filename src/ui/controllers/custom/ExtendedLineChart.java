package ui.controllers.custom;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;

public class ExtendedLineChart extends Button { //<X, Y>{

    public ExtendedLineChart(Axis /*<X>*/ xAxis, Axis /*<Y>*/ yAxis) {
        super(); //xAxis, yAxis);
        this.setOnMouseEntered(
                (event) -> {
                    System.out.println(event.getX() + "\t" + event.getY());
                });
    }
}
