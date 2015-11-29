package connection;

//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.FileNotFoundException;
//import java.util.Scanner;

import ui.controllers.GUI;
//import ui.terminal.TerminalScanner;


public class AutoPinger implements Runnable {

	private Connection connection;
	private boolean running = true;
	
	public AutoPinger(Connection connection){
		this.connection = connection;
		new Thread(this).start();
		
	}
	
	@Override
	public void run() {
	
		while(GUI.running && this.running){
			if (connection != null && connection.isOpen()) {
				if (connection.ping()) {
					System.out.println("Ping");
				} else {
					System.out.println("Ping Failure");
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void setConnection(Connection connection){
		this.connection = connection;
	}
	
	public synchronized void setRunning(boolean val){
		if(running == val)
			return;
		
		if(!running){
			running = true;
			new Thread(this).start();
		}else{
			running = false;
		}
	}

}
