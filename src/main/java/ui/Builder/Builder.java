package ui.Builder;

import java.awt.Event;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Popup;
import ui.controllers.GUI;
import ui.Builder.MenuItems.EntryType;
import ui.controllers.EATab;
import util.BooleanHolder;
import util.ComboHolder;
import util.DoubleHolder;
import util.IntegerHolder;
import util.StringHolder;

public class Builder {
	
	private final EATab tabController;
	private static ArrayList<Control> constants = new ArrayList<Control>();
	
	/**
	 * Makes a new Builder for this controller.
	 * @param tabController the Controller that made this Builder
	 */
	public Builder(EATab tabController){
		this.tabController = tabController;
	}
	
	/**
	 * Builds a GridPane from the inputed object's MenuItems Object. The inputed object MUST implement
	 * HasMenu.
	 * 
	 * @param sIn the Object from which to populate a GridPane
	 * @param grid the GridPane that all of this will be added to
	 * @return the new GridPane
	 */
	public GridPane build(Object sIn, GridPane grid){
		if(!(sIn instanceof TabMenu)){
			System.out.println("ERROR: Builder tried to build an object that cannot be made into a menu: " + sIn);
			return grid;
		}
			
		TabMenu client = ((TabMenu)sIn);
		MenuItems menuItems = client.getMenuItems();
		if(menuItems == null || menuItems.isEmpty()){
			tabController.setValidity(client.check(), sIn);
			return grid;
		}
		for(int i = 0; i < menuItems.size(); i++){
			
			if(menuItems.getType(i) == EntryType.SEPARATOR){
				Line line = new Line();
				line.setEndX(100);
				line.setStroke(Paint.valueOf("B4B4B4"));
				GridPane.setColumnSpan(line, 2);
				GridPane.setRowSpan(line, 1);
				GridPane.setHalignment(line, HPos.CENTER);
				GridPane.setValignment(line, VPos.CENTER);
				GridPane.setMargin(line, new Insets(30));
				grid.add(line, 0, i + 1);
				continue;
			}
			String title = menuItems.getTitle(i).trim();
			if(!title.substring(title.length()-1).equals(":")){
				title += ":";
			}
			Label label = new Label(title);
			label.setEllipsisString("..");
			Popup popup = new Popup();
			Label popupMessage = new Label(title);
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
			
			switch(menuItems.getType(i)){
			case CHECKBOX:
				//grid.getRowConstraints().add(r);
				CheckBox cb = new CheckBox();
				//GridPane gp = new GridPane();
				//gp.setAlignment(Pos.CENTER);
				//gp.add(cb, 0, 0);
				cb.setAlignment(Pos.CENTER_RIGHT);
				BooleanHolder bol = (BooleanHolder)menuItems.getVariable(i);
				cb.setOnAction((event) -> {
					bol.setValue(cb.isSelected());
					tabController.setValidity(client.check(), sIn);
					tabController.setChanged(true);
				});
				if(!menuItems.getChangable(i)){
					constants.add((Control)cb);
				}
				grid.add(cb, 1, i + 1);
				break;
			case SLIDER:
				//grid.getRowConstraints().add(r);
				HBox hb = new HBox();
				hb.setSpacing(5);
				hb.setAlignment(Pos.CENTER_RIGHT);
				hb.prefWidth(129);
				Slider s = new Slider();
				s.setPrefWidth(129);
				HBox.setHgrow(s, Priority.ALWAYS);
				TextField tf = new TextField();
				HBox.setHgrow(tf, Priority.ALWAYS);
				Constraint c = menuItems.getConstraint(i);
				int digits = 0;
				if(c.getDigitType() == Constraint.Type.DOUBLE){
					DoubleHolder output = (DoubleHolder)menuItems.getVariable(i);
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
					    tabController.setChanged(true);
					    tabController.setValidity(client.check(), sIn);
					});
					tf.setOnAction((event) -> {
						System.out.println(event);
						double in = c.getMinDouble();
						try{
							in = Double.parseDouble(tf.getText());
						}catch(NumberFormatException ex){}
						if(in < c.getMinInt()){
							in = c.getMinInt();
							tf.setText("" + in);
						}else if(in > c.getMaxInt()){
							in = c.getMaxInt();
							tf.setText("" + in);
						}
						s.setValue(in);
						output.setValue(Math.round(in * (1/dInc) ) / (1/dInc));
						tabController.setValidity(client.check(), sIn);
						tabController.setChanged(true);
					});
					
					
				}else{
					IntegerHolder output = (IntegerHolder)menuItems.getVariable(i);
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
					    tabController.setChanged(true);
					});
					tf.setOnAction((event) ->{
						System.out.println("This: " + Event.KEY_RELEASE);
						int in = c.getMinInt();
						try{
							in = Integer.parseInt(tf.getText());
						}catch(NumberFormatException ex){}
						if(in < c.getMinInt()){
							in = c.getMinInt();
							tf.setText("" + in);
						}else if(in > c.getMaxInt()){
							in = c.getMaxInt();
							tf.setText("" + in);
						}
						s.setValue(in);
						output.setValue(in);
						tabController.setValidity(client.check(), sIn);
						tabController.setChanged(true);
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
				if(!menuItems.getChangable(i)){
					constants.add((Control)s);
					constants.add((Control)tf);
				}
				grid.add(hb, 1, i + 1);
				break;
			case TEXT:
				TextField textfield = new TextField();
				StringHolder output = (StringHolder)menuItems.getVariable(i);
				if(output.initialized()){
					textfield.setText(output.getValue());
				}
				textfield.textProperty().addListener((observable, oldValue, newValue) -> {
				    output.setValue(newValue);
				    tabController.setValidity(client.check(), sIn);
				    tabController.setChanged(true);
				});
				if(!menuItems.getChangable(i)){
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
				lab.setText(menuItems.getVariable(i).toString());
				grid.add(lab, 1, i + 1);
				break;
			case COMBOBOX:
				hb = new HBox();
				ComboBox<Object> comboB = new ComboBox<Object>();
				comboB.setPromptText("Select a Value");
				comboB.setPrefWidth(200);
				comboB.setMaxWidth(200);
				//comboB.setMinWidth(200);
				hb.getChildren().add(comboB);
				HBox.setHgrow(comboB, Priority.ALWAYS);
				
				ComboHolder ch = ((ComboHolder)menuItems.getVariable(i));
				ch.setComboBox(comboB);
				comboB.getItems().addAll(ch.getTitles());
				
				if(ch.getFocusObject() != null){
					comboB.getSelectionModel().select(ch.getFocusObject());
				}
				comboB.setOnAction((event) -> {
					ch.setFocus(comboB.getValue());
					tabController.setValidity(client.check(), sIn);
					tabController.setChanged(true);
				});
				if(!menuItems.getChangable(i)){
					constants.add((Control)comboB);
				}
				
				grid.add(hb, 1, i + 1);
				break;
			}
		}
		tabController.setValidity(client.check(), sIn);
		return grid;
	}
	
	/**
	 * Sets the Changeable object's enable function to the inputed value
	 * @param val the value to set the Changeable object's enable function to
	 */
	public void setChangeable(boolean val){
		for(Control c : constants){
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
