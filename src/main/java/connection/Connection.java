package connection;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;

public class Connection {
	
	private boolean isOpen = false;
	private SocketWrapper connection;
    private byte[] id;
	
	private final ProgressBar serverStatusBar;
	private final TextField serverStatusText;
	
	public Connection(String hostName, int routingPort, ProgressBar serverStatusBar, TextField serverStatusText){
		System.out.println("Open connection...");
		System.out.println("\tServer: " + hostName + ":" + routingPort);
		this.serverStatusBar = serverStatusBar;
		this.serverStatusText = serverStatusText;
		connection = new SocketWrapper(hostName, routingPort);
        id = new byte[SocketWrapper.idLength];
        new Random().nextBytes(id);
        isOpen = false;
		
	}
	
	public int open(){
		int result = connection.connect(id);
        isOpen = (result == 0 || result == 1);
        if (isOpen)
        	serverStatusText.setText("Open");
        return result;
	}
	
	public void close(){
		if (isOpen) {
            connection.sendMessage(SocketWrapper.EXIT, SocketWrapper.EMPTY_ARRAY);
        }
        connection.close();
		isOpen = false;
		serverStatusText.setText("Closed");
	}
	
	public synchronized boolean send(byte[] message){
		isOpen = connection.sendMessage(SocketWrapper.GENERIC, message);
        return isOpen;
	}
	
	public synchronized boolean send(Object obj){
		return true;
	}
	
	public byte[] read(){
		return connection.parseInput();
	}
	
	public boolean sustain(){
		return sustain(5);
	}
	
	public boolean sustain(int numAttempts){
		if (connection.sendMessage(SocketWrapper.PING, id)) {
            isOpen = true;
            System.out.println("2");
            return true;
        } else {
            int result;
            do {
                numAttempts--;
                result = open();
                switch (result) {
                case 0: // Success
                case 1: // Already Open
                    isOpen = true;
                    System.out.println("3");
                    return true;
                case 2: // No could not reach routing server, wait and try again
                case 3: // No open port, wait and try again
                case 4: // Invalid port received, wait and try again
                    try {Thread.sleep(500);} catch(Exception e) {}
                    break;
                case 5: // No response from server on the given port.
                default: // Unknown error.
                    isOpen = false;
                    System.out.println("4");
                    return false;
                }
            } while(result > 1 && numAttempts >= 0);
        }
        isOpen = false;
        System.out.println("5");
        return false;
	}
	
	public int avaliable(){
		if (isOpen) {
            try {
                return connection.available();
            } catch (IOException e) {
                return 0;
            }
        } else {
            return 0;
        }
	}
	
	public boolean isAvaliable(){
		return true;
	}
	
	public synchronized boolean ping(){
		isOpen = connection.sendMessage(SocketWrapper.PING, id);
        return isOpen;
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
	public String getHostName() {
        return connection.hostName;
    }

    public int getRoutingPort() {
        return connection.routingPort;
    }

    public int getPort() {
        return connection.portNumber;
    }
}
