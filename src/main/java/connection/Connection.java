package connection;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class Connection {
	boolean isOpen = false;
	
	private final ProgressBar serverStatusBar;
	private final TextField serverStatusText;
	
	public Connection(String hostName, int routingPort, ProgressBar serverStatusBar, TextField serverStatusText){
		System.out.println("Open connection...");
		System.out.println("\tServer: " + hostName + ":" + routingPort);
		this.serverStatusBar = serverStatusBar;
		this.serverStatusText = serverStatusText;
		
	}
	
	public int open(){
		isOpen = true;
		serverStatusText.setText("Open");
		return 0;
	}
	
	public void close(){
		isOpen = false;
		serverStatusText.setText("Closed");
	}
	
	public synchronized boolean send(byte[] message){
		return true;
	}
	
	public synchronized boolean send(Object obj){
		return true;
	}
	
	public byte[] read(){
		return new byte[]{0x1};
	}
	
	public boolean sustain(){
		return true;
	}
	
	public boolean sustain(int numAttempts){
		return true;
	}
	
	public int avaliable(){
		return 0;
	}
	
	public boolean isAvaliable(){
		return true;
	}
	
	public synchronized boolean ping(){
		return true;
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
	public String getHostName(){
		return "google.com";
	}
	
	public int getRoutingPort(){
		return 1234;
	}
	
	public int getPort(){
		return 4321;
	}
	
	
}
