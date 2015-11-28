package connection;

import java.util.Scanner;

import ui.controllers.GUI;

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
			
			if(connection != null && connection.isOpen() && connection.ping())
				System.out.println("Ping");
			else
				System.out.println("Ping Failure");
			try {
				Thread.sleep(5000);
				Scanner reader = new Scanner(System.in);
				
				System.out.println("Result: " + reader.nextLine());
				
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
