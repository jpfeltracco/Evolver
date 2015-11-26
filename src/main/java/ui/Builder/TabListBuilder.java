package ui.Builder;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Popup;
import ui.controllers.EATab;
import ui.controllers.FXController;
import ui.controllers.GUI;
import ui.controllers.custom.TetheredButton;
import ui.controllers.custom.TetheredProgressBar;

public class TabListBuilder {

	private GridPane area;
	private ArrayList<EATab> eaTabs = new ArrayList<EATab>();
	private FXController fxController;
	
	public TabListBuilder(FXController fxController){
		this.fxController = fxController;
	}
	
	
	public GridPane addEATab(EATab eaTab){
		
		System.out.println("Contained: " + eaTabs.contains(eaTab));
		
		if(!eaTabs.contains(eaTab)){
			area = getNewGrid();
			eaTabs.add(eaTab);
			return build();
		}
		return null;
	}
	
	public GridPane removeEATab(EATab eaTab){
		if(eaTabs.contains(eaTab)){
			eaTabs.remove(eaTab);
			return build();
		}
		return null;
	}
	
	public GridPane build(){
		area = getNewGrid();
		
		for(int i = 0; i < eaTabs.size(); i++){
			EATab eaTab = eaTabs.get(i);
			
			Label label = new Label(eaTab.getTabText());
			Popup popup = new Popup();
			Label popupMessage = new Label(label.getText());
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
			
			
			
			
			
			
			Line line = new Line();
			line.setEndX(100);
			line.setStroke(Paint.valueOf("B4B4B4"));
			GridPane.setColumnSpan(line, 2);
			GridPane.setRowSpan(line, 1);
			GridPane.setHalignment(line, HPos.CENTER);
			GridPane.setValignment(line, VPos.CENTER);
			area.add(line, 0, i*2+1);
			
			
			
			
			TetheredProgressBar progress = new TetheredProgressBar(eaTab.progressBar);
			
			StackPane sp = new StackPane();
			sp.getChildren().add(label);
			
			StackPane sp2 = new StackPane();
			sp2.getChildren().add(progress);
			
			VBox vbox = new VBox();
			vbox.getChildren().addAll(sp, sp2);
			
			HBox hbox = new HBox();
			
			TetheredButton button = new TetheredButton(eaTab.startButton);
			
			MenuButton mb = new MenuButton();
			mb.setText("...");
			MenuItem save = new MenuItem("Save");
			save.setOnAction((event) -> {
				System.out.println("SAVE");
				fxController.saveEvolution(eaTab);
			});
			
			MenuItem close = new MenuItem("Close");
			close.setOnAction((event) -> {
				fxController.closeTab(eaTab);
			});
			mb.getItems().addAll(save, close, new MenuItem("Send"));
			
			hbox.getChildren().addAll(button, mb);
			hbox.setSpacing(5);
			
			area.add(vbox, 0, i*2);
			area.add(hbox, 1, i*2);
			
			//button = null;
		}
		
		return area;
	}
	
	
	private GridPane getNewGrid(){
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(5);
		//grid.setGridLinesVisible(true);
		grid.paddingProperty().set(new Insets(5));
		
		//GridPane.setVgrow(grid, Priority.ALWAYS);
		//grid.setMinWidth(500);
		
		ColumnConstraints col = new ColumnConstraints();
		
		col.setMinWidth(10);
		col.setHgrow(Priority.SOMETIMES);
		col.setFillWidth(true);
		
		//col.setPercentWidth(1);
		col.setPrefWidth(100);
		
		grid.getColumnConstraints().addAll(col, col);
		
		//------
		
		RowConstraints row1 = new RowConstraints();
		RowConstraints row2 = new RowConstraints();
		
		row1.setMinHeight(10);
		row1.setVgrow(Priority.SOMETIMES);
		row1.setFillHeight(true);
		row1.setPrefHeight(30);
		
		row2.setMinHeight(10);
		row2.setVgrow(Priority.SOMETIMES);
		row2.setFillHeight(true);
		row2.setValignment(VPos.CENTER);
		
		
		grid.getRowConstraints().addAll(row1, row2);
		
		//ColumnConstraints c = new ColumnConstraints();
		//c.setHgrow(Priority.SOMETIMES);
		//c.setMaxWidth(173.0);
		//c.setMinWidth(10.0);
		//c.setPrefWidth(124.0);
		//grid.getColumnConstraints().add(c);
		
		//c = new ColumnConstraints();
		//c.setHalignment(HPos.RIGHT);
		//c.setHgrow(Priority.NEVER);
		//grid.getColumnConstraints().add(c);
		
		//RowConstraints r = new RowConstraints();
		//r.setVgrow(Priority.NEVER);
		//grid.getRowConstraints().add(r);
		
		return grid;
	}
	
	
	
}
