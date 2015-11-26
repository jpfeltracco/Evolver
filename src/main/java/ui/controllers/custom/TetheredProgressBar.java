package ui.controllers.custom;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Popup;
import ui.controllers.GUI;

public class TetheredProgressBar extends ProgressBar{
	private TetheredProgressBar master;
	private Label label;
	private ArrayList<TetheredProgressBar> mimic = new ArrayList<TetheredProgressBar>(10);
	
	public TetheredProgressBar(){
		super();
		init();
	}
	
	public TetheredProgressBar(TetheredProgressBar tb){
		super();
		master = tb;
		System.out.println("MASTER: " + master);
		setProgress(master.getProgress());
		master.addMimic(this);
		init();
	}
	
	public TetheredProgressBar(double d){
		super(d);
		init();
	}
	
	public void setLabel(Label l){
		label = l;
	}
	
	public void setTetherProgress(double d){
		if(d == -1)
			setText("Complete");
		else
			popupMessage.setText("" + round(d*100) + "%");
		
		if(master == null){
			progressProperty().set(d);
			for(TetheredProgressBar bar : mimic){
				if(bar == null){
					mimic.remove(bar);
					continue;
				}
				if(d == -1)
					bar.mimic(d,"Complete");
				else
					bar.mimic(d,"" + round(d*100) + "%");
			}
		}
	}
	
	public void setTetherProgress(double d, String s){
		if(master == null){
			progressProperty().set(d);
			setText(s);
			for(TetheredProgressBar bar : mimic){
				if(bar == null){
					mimic.remove(bar);
					continue;
				}
				bar.mimic(d, s);
			}
		}
	}
	
	public void setMaster(TetheredProgressBar tb){
		master = tb;
	}
	
	public void addMimic(TetheredProgressBar tb){
		mimic.add(tb);
	}
	
	public void mimic(double d, String s){
		super.setProgress(d);
		setText(s);
	}
	
	public void setText(String s){
		popupMessage.setText(s);
		if(label != null)
			label.setText(s);
	}
	
	Popup popup = new Popup();
	Label popupMessage;
	public void init(){
		
		popupMessage = new Label();
		popupMessage.getStylesheets().add("./ui/css/style.css");
		popupMessage.getStyleClass().add("popup");
		
		this.setOnMouseEntered(event -> {
			System.out.println(getProgress());
			if(popupMessage.getText().length() != 0 && getProgress() != -1){
				popup.setAnchorX(event.getScreenX() + 20);
				popup.setAnchorY(event.getScreenY());
				popup.setAutoFix(true);
			    popup.setHideOnEscape(true);
				popup.getContent().clear();
				popup.getContent().add(popupMessage);
				popup.show(GUI.stage);
			}
		});
		
		this.setOnMouseExited(event -> {
			popup.hide();
		});
	}
	
	private double round(double d){
		return Math.round(d * 100.0) / 100.0;
	}
}


