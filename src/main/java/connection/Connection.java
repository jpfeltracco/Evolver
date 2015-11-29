package connection;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Connection forms the bridge between a SocketWrapper and the rest of Evolver.
 * @author Norris Nicholson
 * @version 1.0
 * @see SocketWrapper.java
 */
public class Connection {
	
	private boolean isOpen = false;
	private SocketWrapper connection;
    private byte[] id;
	
	private final ProgressBar serverStatusBar;
	private final TextField serverStatusText;
	private AutoPinger autoPinger;
	
	/**
	 * verbose sets this SocketWrapper to verbose mode. When it is false, nothing
	 * will be printed
	 */
	public boolean verbose = false;
	
	/**
	 * Creates a new instance of Connection with the given params.
	 * @param hostName The host name of the server to connect to (eg. "nornick3.zapto.org")
	 * @param routingPort The routing port on the server (eg. 2223)
	 * @param serverStatusBar The status bar in the GUI
	 * @param serverStatusText the status text in the GUI
	 */
	public Connection(String hostName, int routingPort, ProgressBar serverStatusBar, TextField serverStatusText){
		System.out.println("Open connection...");
		System.out.println("\tServer: " + hostName + ":" + routingPort);
		this.serverStatusBar = serverStatusBar;
		this.serverStatusText = serverStatusText;
		connection = new SocketWrapper(hostName, routingPort);
        id = new byte[SocketWrapper.idLength];
        new Random().nextBytes(id);
        isOpen = false;
        autoPinger = new AutoPinger(this);
	}
	
	/**
	 * Opens a connection with the server by obtaining a service port and connecting.
	 * Returns:
	 *  0 : Success - established a new connection
	 *  1 : Success - a connection was already open with the host on a service port
	 *  2 : Failure - could not reach the server on the given host
	 *  3 : Failure - contacted the routing server but there were no available service ports
	 *  4 : Failure - invalid port received. Might be a message mismatch
	 *  5 : Failure - valid port received but was unable to connect to it. Might be a server problem
	 * @param id the unique identifier for this instance of Evolve.
	 * @return an escape integer, as specified above.
	 */
	public int open(){
		int result = connection.connect(id);
        isOpen = (result == 0 || result == 1);
        if (isOpen) {
        	serverStatusText.setText("Open");
        	autoPinger.setRunning(true);
        }
        return result;
	}
	
	/**
	 * Closes the connection by sending an exit command to the server, then disconnecting
	 */
	public void close(){
		autoPinger.setRunning(false);
		if (isOpen) {
            connection.sendMessage(SocketWrapper.EXIT, SocketWrapper.EMPTY_ARRAY);
        }
		
        connection.close();
		isOpen = false;
		serverStatusText.setText("Closed");
	}
	
	/**
	 * Send the given generic byte array to the server with the GENERIC command
	 * @param message a byte array message to send
	 * @return true on success
	 */
	public synchronized boolean send(byte[] message){
		isOpen = connection.sendMessage(SocketWrapper.GENERIC, message);
        return isOpen;
	}
	
	/**
	 * Sends the given object to the server by serializing it and sending as a byte array
	 * with the given command.
	 * @param command The command byte identifier to send with the object
	 * @param obj the object to send
	 * @return true on success
	 */
	public synchronized boolean send(byte command, Object obj){
		return true;
	}
	
	/**
	 * Reads a message from the server. This methods blocks until a message is recieved
	 * @return the message from the server, including the command byte
	 */
	public synchronized byte[] read(){
		return connection.parseInput();
	}
	
	/**
	 * A generic sustain() method that runs sustain(5)
	 * @return true if the connection is sustained
	 */
	public boolean sustain(){
		return sustain(5);
	}
	
	/**
	 * Sustains the connection with the server by sending a ping message. If the server
	 * has disconnected, it will attempt to reconnect. This method can be used to open a
	 * connection with the server, although it is not recommended.
	 * @param numAttempts the number of attempts to contact the server before returning false
	 * @return true on success
	 */
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
                    autoPinger.setRunning(false);
                    System.out.println("4");
                    return false;
                }
            } while(result > 1 && numAttempts >= 0);
        }
        isOpen = false;
        autoPinger.setRunning(false);
        System.out.println("5");
        return false;
	}
	
	/**
	 * returns the number of bytes available for reading
	 * @return the number of bytes available for reading
	 */
	public int avaliable(){
		if (isOpen) {
            return connection.available();
        } else {
            return 0;
        }
	}
	
	/**
	 * returns true if there is a message of sufficient size in the buffer
	 * @return true if there is a message of sufficient size in the buffer
	 */
	public boolean isAvaliable(){
		return avaliable() >= SocketWrapper.minMessageSize;
	}
	
	/**
	 * pings the server and returns the success
	 * @return true on success
	 */
	public synchronized boolean ping(){
		isOpen = connection.sendMessage(SocketWrapper.PING, id);
        return isOpen;
	}
	
	/**
	 * returns the open status of the connection.
	 * @return the open status of the connection
	 */
	public boolean isOpen(){
		return isOpen;
	}
	
	/**
	 * returns the host name of the external server
	 * @return the host name of the external server
	 */
	public String getHostName() {
        return connection.hostName;
    }

	/**
	 * returns the routing port on the external server
	 * @return the routing port on the external server
	 */
    public int getRoutingPort() {
        return connection.routingPort;
    }

    /**
     * returns the current connection port. Will often differ from
     * the routing port
     * @return the current connection port
     */
    public int getPort() {
        return connection.portNumber;
    }
}