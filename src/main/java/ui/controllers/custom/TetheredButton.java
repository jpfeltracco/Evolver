package ui.controllers.custom;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import ui.controllers.GUI;

//@DefaultProperty(value = "startButton")
public class TetheredButton extends Button{

	private TetheredButton master;
	private ArrayList<TetheredButton> mimic = new ArrayList<TetheredButton>(10);
	
	public TetheredButton(){
		super("Button");
	}
	
	public TetheredButton(String args0){
		super(args0);
	}
	
	public TetheredButton(TetheredButton master){
		super();
		this.master = master;
		setText(master.getText());
		master.addMimic(this);
	}
	
	public void setMaster(TetheredButton tb){
		master = tb;
	}
	
	public void addMimic(TetheredButton tb){
		mimic.add(tb);
	}
	
	public void setTetheredDisable(boolean val){
		setDisable(val);
		for(TetheredButton tb : mimic){
			if(tb == null){
				mimic.remove(tb);
				continue;
			}
			tb.setDisable(val);
		}
	}
	
	@Override
	public void fire(){
		super.fire();
		if(master != null){
			master.fire();
		}else if(mimic.size() > 0){
			for(TetheredButton tb : mimic){
				if(tb == null){
					mimic.remove(tb);
					continue;
				}
				tb.mimic();
			}
		}
	}
	
	public void mimic(){
		if(master == null)
			return;
		this.setText(master.getText());
	}
	
	
	
	
	
	
}
