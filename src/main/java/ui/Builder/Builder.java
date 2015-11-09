package ui.Builder;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import ui.controllers.GUI;
import ui.controllers.EATabController;
import ui.controllers.EATabHolder;
import util.BooleanHolder;
import util.ComboHolder;
import util.DoubleHolder;
import util.IntegerHolder;
import util.StringHolder;

public class Builder {
	
	private final EATabHolder tabController;
	private static ArrayList<Control> constants = new ArrayList<Control>();
	
	/**
	 * Makes a new Builder for this controller.
	 * @param tabController the Controller that made this Builder
	 */
	public Builder(EATabHolder tabController){
		this.tabController = tabController;
	}
	
	/**
	 * Builds a GridPane from the inputed object's InputFramework. The inputed object MUST implement
	 * HasMenu.
	 * 
	 * @param sIn the Object from which to populate a GridPane
	 * @param grid the GridPane that all of this will be added to
	 * @return the new GridPane
	 */
	public GridPane build(Object sIn, GridPane grid){
		if(!(sIn instanceof HasMenu)){
			System.out.println("ERROR: Builder tried to build an object that cannot be made into a menu: " + sIn);
			return grid;
		}
			
		HasMenu client = ((HasMenu)sIn);
		InputFramework inputF = client.getFramework();
		if(inputF == null)
			return grid;
		for(int i = 0; i < inputF.size(); i++){
			Label label = new Label(inputF.getTitle(i));
			String message = inputF.getTitle(i);
			Popup popup = new Popup();
			Label popupMessage = new Label(message);
			popupMessage.getStylesheets().add("./ui/css/style.css");
			popupMessage.getStyleClass().add("popup");
			
			label.setOnMouseEntered(event -> {
				popup.setAnchorX(event.getScreenX() + 20);
				popup.setAnchorY(event.getScreenY());
				popup.setAutoFix(true);
			    popup.setHideOnEscape(true);
				popup.getContent().clear();
				popup.getContent().add(popupMessage);
				popup.show(GUI.stage);
			});
			
			label.setOnMouseExited(event -> {
				popup.hide();
			});
			
			grid.add(label, 0, i + 1);
			
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
					tabController.setValidity(client.check(), sIn);
				});
				if(!inputF.getChangable(i)){
					constants.add((Control)cb);
				}
				grid.add(gp, 1, i + 1);
				break;
			case SLIDER:
				//grid.getRowConstraints().add(r);
				HBox hb = new HBox();
				hb.setSpacing(5);
				Slider s = new Slider();
				TextField tf = new TextField();
				Constraint c = inputF.getConstraint(i);
				int digits = 0;
				if(c.getDigitType() == Constraint.Type.DOUBLE){
					DoubleHolder output = (DoubleHolder)inputF.getVariable(i);
					s.setMin(c.getMinDouble());
					s.setMax(c.getMaxDouble());
					if(output.getValue() >= c.getMinDouble() && output.getValue() <= c.getMaxDouble()){
						s.setValue(output.getValue());
					}else{
						output.setValue(c.getMinDouble());
					}
					String doubleIncrement = "0.";
					for(int j = 0; j < c.getDigitCount()-1; j++){
						doubleIncrement += "0";
					}
					doubleIncrement += "1";
					double dInc = Double.parseDouble(doubleIncrement);
					s.setBlockIncrement(dInc);
					String dString = "" + c.getDigitCount();
					digits = String.valueOf(String.format("%."+dString+"f",  c.getMaxDouble())).length();
					tf.setText(String.format("%."+dString+"f", s.valueProperty().floatValue()));
					s.valueProperty().addListener((observable, oldValue, newValue) -> {
					    tf.setText(String.format("%."+dString+"f", newValue.floatValue()));
					    output.setValue(Math.round(newValue.floatValue() * (1/dInc) ) / (1/dInc));
					    
					    tabController.setValidity(client.check(), sIn);
					});
					tf.setOnAction((event) -> {
						double in = c.getMinDouble();
						try{
							in = Double.parseDouble(tf.getText());
						}catch(NumberFormatException ex){}
						if(in < c.getMinDouble())
							in = c.getMinDouble();
						else if(in > c.getMaxDouble())
							in = c.getMaxDouble();
						tf.setText("" + in);
						s.setValue(in);
						output.setValue(Math.round(in * (1/dInc) ) / (1/dInc));
						tabController.setValidity(client.check(), sIn);
					});
					
				}else{
					IntegerHolder output = (IntegerHolder)inputF.getVariable(i);
					s.setMin(c.getMinInt());
					s.setMax(c.getMaxInt());
					if(output.getValue() >= c.getMinInt() && output.getValue() <= c.getMaxInt()){
						s.setValue(output.getValue());
					}else{
						output.setValue(c.getMinInt());
					}
					s.setBlockIncrement(1);
					digits = String.valueOf(c.getMaxInt()).length();
					tf.setText("" + s.valueProperty().intValue());
					s.valueProperty().addListener((observable, oldValue, newValue) -> {
					    tf.setText("" + newValue.intValue());
					    output.setValue(newValue.intValue());
					    tabController.setValidity(client.check(), sIn);
					});
					tf.setOnAction((event) -> {
						int in = c.getMinInt();
						try{
							in = Integer.parseInt(tf.getText());
						}catch(NumberFormatException ex){}
						if(in < c.getMinInt())
							in = c.getMinInt();
						else if(in > c.getMaxInt())
							in = c.getMaxInt();
						tf.setText("" + in);
						s.setValue(in);
						output.setValue(in);
						tabController.setValidity(client.check(), sIn);
					});
					
				}
				
				
				s.setMajorTickUnit((int)  (((c.getMaxDouble() - c.getMinDouble())) / 2 + 1) );
				s.setMinorTickCount(2);
				s.setShowTickMarks(true);
				s.setShowTickLabels(true);
				tf.setMinWidth(16 + digits * 8);
				tf.setMaxWidth(16 + digits * 8);
				hb.getChildren().add(s);
				hb.getChildren().add(tf);
				if(!inputF.getChangable(i)){
					constants.add((Control)s);
					constants.add((Control)tf);
				}
				grid.add(hb, 1, i + 1);
				break;
			case TEXT:
				TextField textfield = new TextField();
				StringHolder output = (StringHolder)inputF.getVariable(i);
				if(output.initialized()){
					textfield.setText(output.getValue());
				}
				textfield.textProperty().addListener((observable, oldValue, newValue) -> {
				    output.setValue(newValue);
				    tabController.setValidity(client.check(), sIn);
				});
				if(!inputF.getChangable(i)){
					constants.add((Control)textfield);
				}
				grid.add(textfield,  1,  i + 1);
				break;
			case LABEL:
				Label lab = new Label();
				lab.maxWidth(198.0);
				lab.setWrapText(true);
				//System.out.println(grid.getRowConstraints().size());
				//RowConstraints rc = new RowConstraints();
				//rc.setVgrow(Priority.NEVER);
				//grid.getRowConstraints().add(rc);
				lab.setText(inputF.getVariable(i).toString());
				grid.add(lab, 1, i + 1);
				break;
			case COMBOBOX:
				ComboBox<Object> comboB = new ComboBox<Object>();
				comboB.setPromptText("Select a Value");
				ComboHolder ch = ((ComboHolder)inputF.getVariable(i));
				ch.setComboBox(comboB);
				comboB.getItems().addAll(ch.getTitles());
				
				if(ch.getFocusObject() != null){
					comboB.getSelectionModel().select(ch.getFocusObject());
				}
				comboB.setOnAction((event) -> {
					ch.setFocus(comboB.getValue());
					tabController.setValidity(client.check(), sIn);
				});
				if(!inputF.getChangable(i)){
					constants.add((Control)comboB);
				}
				grid.add(comboB, 1, i + 1);
				break;
			}
		}
		tabController.setValidity(client.check(), sIn);
		return grid;
	}
	
	/**
	 * Sets the Changable object's enable function to the inputed value
	 * @param val the value to set the Changable object's enable function to
	 */
	public void setChangable(boolean val){
		for(Control c : constants){
			//System.out.println(c);
			c.setDisable(!val);
		}
	}
	
	/**
	 * Adds a Control to the list of Controls that are not able to be changed during
	 * runtime.
	 * @param c the Control to add to the list
	 */
	public void addNonChangable(Control c){
		constants.add(c);
	}
	
}
