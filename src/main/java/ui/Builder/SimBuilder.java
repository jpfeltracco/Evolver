package ui.Builder;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import simulations.Simulation;
import ui.controllers.TabController;
import util.BooleanHolder;
import util.DoubleHolder;
import util.IntegerHolder;

public class SimBuilder {
	private final TabController tc;
	private RowConstraints r;
	public SimBuilder(TabController tc){
		this.tc = tc;
	}
	
	public GridPane build(Simulation sim, GridPane grid, RowConstraints r){
		InputFramework inputF = sim.getFramework();
		if(inputF == null)
			return grid;
		for(int i = 0; i < inputF.size(); i++){
			grid.add(new Label(inputF.getTitle(i)), 0, i + 1);
			switch(inputF.getType(i)){
			case CHECKBOX:
				//grid.getRowConstraints().add(r);
				CheckBox cb = new CheckBox();
				GridPane gp = new GridPane();
				gp.setAlignment(Pos.CENTER);
				gp.add(cb, 0, 0);
				cb.setAlignment(Pos.CENTER);
				BooleanHolder bol = (BooleanHolder)inputF.getVariable(i);
				cb.setOnAction((event) -> {
					bol.setValue(cb.isSelected());
					sim.check();
				});
				grid.add(gp, 1, i + 1);
				break;
			case SLIDER:
				//grid.getRowConstraints().add(r);
				HBox hb = new HBox();
				hb.setSpacing(5);
				Slider s = new Slider();
				Label l = new Label();
				Constraint c = inputF.getConstraint(i);
				int digits = 0;
				if(c.getDigitType() == Constraint.Type.DOUBLE){
					DoubleHolder output = (DoubleHolder)inputF.getVariable(i);
					s.setMin(c.getMinDouble());
					s.setMax(c.getMaxDouble());
					String doubleIncrement = "0.";
					for(int j = 0; j < c.getDigitCount()-1; j++){
						doubleIncrement += "0";
					}
					doubleIncrement += "1";
					double dInc = Double.parseDouble(doubleIncrement);
					s.setBlockIncrement(dInc);
					String dString = "" + c.getDigitCount();
					digits = String.valueOf(String.format("%."+dString+"f",  c.getMaxDouble())).length();
					s.valueProperty().addListener((observable, oldValue, newValue) -> {
					    l.setText(String.format("%."+dString+"f", newValue.floatValue()));
					    output.setValue(Math.round(newValue.floatValue() * (1/dInc) ) / (1/dInc));
					    sim.check();
					});
					l.setText(String.format("%.2f", s.valueProperty().floatValue()));
				}else{
					IntegerHolder output = (IntegerHolder)inputF.getVariable(i);
					s.setMin(c.getMinInt());
					s.setMax(c.getMaxInt());
					s.setBlockIncrement(1);
					digits = String.valueOf(c.getMaxInt()).length();
					s.valueProperty().addListener((observable, oldValue, newValue) -> {
					    l.setText("" + newValue.intValue());
					    output.setValue(newValue.intValue());
					    sim.check();
					});
					l.setText("" + s.valueProperty().intValue());
				}
				s.setMajorTickUnit((int)((c.getMaxDouble() - c.getMinDouble())) / 2);
				s.setMinorTickCount(2);
				s.setShowTickMarks(true);
				s.setShowTickLabels(true);
				l.setMinWidth(digits * 9);
				hb.getChildren().add(s);
				hb.getChildren().add(l);
				grid.add(hb, 1, i + 1);
				break;
			case TEXT:
				//grid.getRowConstraints().add(r);
				break;
			case LABEL:
				Label lab = new Label();
				lab.maxWidth(198.0);
				lab.setWrapText(true);
				System.out.println(grid.getRowConstraints().size());
				//RowConstraints rc = new RowConstraints();
				//rc.setVgrow(Priority.NEVER);
				//grid.getRowConstraints().add(rc);
				lab.setText(inputF.getVariable(i).toString());
				grid.add(lab, 1, i + 1);
				break;
			}
		}
		return grid;
	}
	
	private Object add(String t){
		return null;
		/*switch(t.toLowerCase().trim()){
		case "checkbox":
			CheckBox cb = new CheckBox();
			cb.
			return ;
		case "slider":
			return null;
		case "text":
			return null;
		}*/
	}
}

/*	Types of Possible elements
 * 		Check Box
 * 		Slider
 * 		Text Input
 */
