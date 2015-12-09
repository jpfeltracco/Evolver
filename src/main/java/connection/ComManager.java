package connection;

import java.net.URL;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import ui.controllers.GUI;

public class ComManager implements Runnable{
	private final TextField addressField;
	private final TextField portField;
	private final CheckBox acceptEvolutions;
	private final CheckBox acceptBenchmarks;
	private final Button connectButton;
	private final ProgressBar serverStatusBar;
	private final TextField serverStatusText;
	private Connection connection;
	
	
	protected boolean portValid = true;
	protected boolean addrValid = true;
	
	protected int selectedPort;
	protected String selectedAddr = "";
	
	public ComManager(TextField addressField, TextField portField, CheckBox acceptEvolutions
			, CheckBox acceptBenchmarks, Button connectButton, ProgressBar serverStatusBar, TextField serverStatusText){
		this.addressField = addressField;
		this.portField = portField;
		this.acceptEvolutions = acceptEvolutions;
		this.acceptBenchmarks = acceptBenchmarks;
		this.connectButton = connectButton;
		this.serverStatusBar = serverStatusBar;
		this.serverStatusText = serverStatusText;
		
		selectedAddr = addressField.getPromptText();
		selectedPort = Integer.parseInt(portField.getPromptText());
		
		
		connectButton.setDisable(false);
		
		connectButton.setOnAction((event) ->{
			if(connection != null && connection.isOpen()){
				connection.close();
				
				connection = null;
				connectButton.setText("Connect");
			}else{
				new Thread(() -> {
					setConnection(new Connection(selectedAddr, selectedPort, serverStatusBar, serverStatusText));
					if(connection.open() == 0)
						Platform.runLater(() -> {
							connectButton.setText("Close");
						});
				}).start();

			}
		});
		
		addressField.setOnAction((event) -> {
			
			String addr = addressField.getText();
			if(addr.length() == 0){
				addr = addressField.getPromptText();
			}
			if(!addr.contains(".")){
				addrValid = false;
				checkFields();
				return;
			}
			
			selectedAddr = addr;
			addrValid = true;
			checkFields();
		});
		
		portField.setOnAction((event) -> {
			String in;
			if(portField.getText().length() == 0)
				in = portField.getPromptText();
			else
				in = portField.getText();
			try{
				selectedPort = Integer.parseInt(in);
				portValid = true;
			}catch(NumberFormatException ex){
				portValid = false;
			}
			checkFields();
			
		});
		
		/*Platform.runLater(new Runnable(){
			@Override
			public void run() {
				
			}
			
		});*/
	}
	
	public synchronized void setConnection(Connection c){
		connection = c;
	}
	
	protected void checkFields(){
		System.out.println("Valid: " + (portValid && addrValid));
		connectButton.setDisable(!(portValid && addrValid));
	}
	
	@Override
	public void run() {
		
		while(GUI.running){
			if(connection != null && connection.isOpen()){
				System.out.println("Connection Good");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}


