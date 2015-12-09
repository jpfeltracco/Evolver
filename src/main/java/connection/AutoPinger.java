package connection;
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
				e.printStackTrace();
				// we dont care about printing this
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
