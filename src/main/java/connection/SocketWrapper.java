package connection;
/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import java.io.OutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

// Message structure:
// [1][2][3][4][5][6][7][8] [1][2][3][4] [1] [1][2][3][...][len]
// |_______Checksum_______| |__Length__|  |  |_____Message_____|
//                                     ___|___
//

public class SocketWrapper {

	public int routingPort;
	public int portNumber;
	public String hostName;
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private OutputStream out;
	private InputStream in;

	//private Queue<byte> byteQueue;

	private static boolean verbose = true;

	public static final byte EXIT = 0x01;
	public static final byte REQUEST_PORT = 0x02;
	public static final byte NO_OPEN_PORTS = 0x03;
	public static final byte GENERIC = 0x04;
	public static final byte EMPTY = 0x05;
	public static final byte PORT = 0x06;
	public static final byte PING = 0x07;

	public static final byte[] EMPTY_ARRAY = {EMPTY};

	public static final int checksumLength = 8;
	public static final int sizeIentLength = 4;
	public static final int checksumTimeout = 5000;
	public static final int readTimeout = 5000;

	public static final int idLength = 4;


	public static final int minMessageSize = checksumLength + sizeIentLength + 1;


	public SocketWrapper(String hostName, int routingPort) {
		this.routingPort = routingPort;
		this.portNumber = routingPort;
		this.hostName = hostName;
		println("[Constructor] Connection constructor starting: " + hostName + ":" + routingPort);
		//byteQueue = new Queue<byte>();
	}

	// 0 = success - got new connection
	// 1 = success - Already connected
	// 2 = could not reach server
	// 3 = no open ports
	// 4 = invalid port recieved
	// 5 = valid port recieved but unble to open connection with it
	public int connect(byte[] id) {
		if(isOpen() && portNumber != routingPort)
			return 1;

		portNumber = routingPort;
		int connectionPort = -1;

        if(open()) {
        	if (sendMessage(REQUEST_PORT, id)) {
        		byte[] input;
	        	if ((input = parseInput()) != null) {
	        		if(input[0] == NO_OPEN_PORTS) {
	        			println("[Connect] Couldnt get a connection port");
	        			close();
	        			return 3;
	        		} else if(input[0] == PORT) {
	        			connectionPort = ByteBuffer.wrap(removeCommand(input)).getInt();
	        			if (connectionPort == -1) {
	        				println("[Connect] Connection port error! (Invalid message)");
	        				close();
	        				return 4;
	        			}
	        			println("[Connect] Got Port " + connectionPort +". Closing..");
	        		}
	        	} else {
	        		println("[Connect] Input was null");
	        		close();
	        		return 4;
	        	}
	        	close();
	        } else {
	        	println("[Connect] Could not contact routing server.");
	        	close();
	        	return 2;
	        }
        } else {
        	println("[Connect] Cant connect to routingPort");
        	return 2;
        }

    	println("[Connect] Connection to port: " + connectionPort);
        portNumber = connectionPort;

        try {Thread.sleep(500);}catch(Exception e) {}

        if (!open()) {
	    	println("[Connect] Unable to connect on the given port");
	    	return 5;
	    }
	    return 0;
	}

	public void close(){
		println("[Close] Closing connection");
		println("[Close] Current status: " + isOpen());
		if (isOpen()) {
			try {
				print("[Close] Closing sockets...");
				clientSocket.close();
				println("Done.");
				clientSocket = null;
				out = null;
				in = null;
			} catch (IOException e) {
				println("ERROR");
				if (verbose)
					println("[Close] " + e.getMessage(),true);
			}
		} else {
			println("[Close] Already closed.");
		}
	}

	public boolean open() {
		println("[Open] Opening Connection");
		println("[Open] Current status: " + isOpen());
		if (!isOpen()) {
			if (openSocket()) {
				try {
					print("[Open] Socket Opening Complete. Opening Buffers and Writers...");
		            //out = new PrintWriter(clientSocket.getOutputStream(), true);
		            out = clientSocket.getOutputStream();
		            //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		            in = clientSocket.getInputStream();
		            println("Done.");
				}catch (IOException e) {
					println("ERROR.");
		        	println(e.getMessage(),true);
		        	println("[Open] Open Failure");
		        	return false;
		        }
			} else {
				println("[Open] Open Failure. Could not open Sockets.");
				return false;
			}
		} else {
			println("[Open] Already Open.");
			return true;
		}
		println("[Open] Open Success");
		return true;
	}

	private boolean openSocket() {
		try {
			print("[Socket] Opening Sockets (Waiting for connection)...");
            clientSocket = new Socket(hostName, portNumber);
            println("Done.");
            println("[Socket] Open Socket Success");
		} catch (Exception e) {
			println("ERROR.");
			println(e.getMessage(),true);
			println("[Socket] Open Socket Failure");
			return false;
		}
		return true;
	}

	public boolean inputReady(){
		//println("[Input] Checking Input Status");
    	try {
    		if (in != null)
	    		return in.available() > 0;
	    	else
	    		return false;
    	} catch (IOException e) {
    		if (verbose) {
    			println(e.getMessage(),true);
    		}
    	}
        return false;
    }

	public byte[] parseInput(){
		println("[Parse] Parsing Input");
		println("[Parse] Current status: " + isOpen());

		byte[] checksum;
		byte[] msg;
		byte[] len;

		if (!isOpen()) {
			return null;
		}

		try {

			while(in != null && in.available() < minMessageSize) {
				Thread.sleep(10);
			}

			if (in == null) {
				println("[Parse] Null input buffer");
				return null;
			}

			print("[Parse] Reading input...");

			// Checksum
			checksum = new byte[checksumLength];
			in.read(checksum, 0, checksumLength);

			// len
			len = new byte[sizeIentLength];
			in.read(len, 0, sizeIentLength);

			// Get the correct size of the message to read
			int correctMessageSize = ByteBuffer.wrap(len).getInt();

			// msg
			int time = 0;

			// Wait until there are at least CorrectMessageSize bytes available
			while (in.available() < correctMessageSize && time < readTimeout) {
  				time += 10;
  				try {Thread.sleep(10);}catch(Exception e){}
  			}

  			// If there are enough bytes (the message is intact), read it
  			if (in.available() >= correctMessageSize) {
  				msg = new byte[correctMessageSize];
				in.read(msg, 0, correctMessageSize);

  			} else {
  				// Otherwise, return null
  				println("ERROR.");
  				println("[Parse] Message timeout. Closing connection. Returning null");
  				close();
  				return null;
  			}

			println("Done.");
		} catch (Exception e) {
			println("ERROR.");
			println("[Parse] " + e.getMessage());
			return null;
		}


		//byte[] checksum = new byte[checksumLength];
		//byte[] messageOut = new byte[msg.length - checksumLength];

		//System.arraycopy(msg, checksumLength, messageOut, 0, msg.length - checksumLength);
		//System.arraycopy(msg, 0, checksum, 0, checksumLength);

		if (verbose) {
			switch(msg[0]) {
				case EXIT:
					println("[Parse] Found EXIT Command");
					break;
				case REQUEST_PORT:
					println("[Parse] Found REQUEST_PORT Command");
					break;
				case NO_OPEN_PORTS:
					println("[Parse] Found NO_OPEN_PORTS Command");
					break;
				case GENERIC:
					println("[Parse] Found GENERIC Command");
					break;
				case EMPTY:
					println("[Parse] Found EMPTY Command");
					break;
				default:
					println("[Parse] Unknown Command: " + Integer.toHexString(msg[0]));
					break;
			}

			print("[Parse] Message (plus command): ");
	  		for (Byte b : msg)
	  			System.out.print(b.toString() + " ");
	  		System.out.println();

			print("[Parse] Message checksum: ");
			for (Byte b : checksum)
	  			System.out.print(b.toString() + " ");
	  		System.out.println();

	  		print("[Parse] Message length: ");
			for (Byte b : len)
	  			System.out.print(b.toString() + " ");
	  		System.out.println();
	  	}

		if (msg[0]==EXIT) {
			println("[Parse] Received Exit Command. No checksum response");
		} else {
			print("[Parse] Responding with checksum...");
			try {
				out.write(checksum);
				println("Done.");
			} catch (IOException e) {
				println("ERROR.");
				println("[Parse] IOException while sending checksum return");
				println("[Parse] " + e.getMessage());
			}
		}
		return msg;
    }

    public boolean sendMessage(byte[] message) {
    	return sendMessage(GENERIC, message);
    }

	public boolean sendMessage(byte command, byte[] message){

		// if the message is null, fill it with an empty charactor (To keep the peace)
		if (message == null) {
			message = EMPTY_ARRAY;

			// if both command and message is null, something is wrong (no message)
			if (command == 0x00) {
				println("[Message] Attempted to send a null",true);
				return false;
			}
		}

		if (!isOpen()) {
			println("[Message] Not Connected!",true);
			return false;
		}

		byte[] checksum = new byte[checksumLength];
		new Random().nextBytes(checksum);

		// Must be large enough to include the command byte as well
		byte[] output = new byte[message.length + checksumLength + 1 + sizeIentLength];

		byte[] length = ByteBuffer.allocate(sizeIentLength).putInt(message.length + 1).array();

		System.arraycopy(checksum, 0, output, 0, checksumLength);
		System.arraycopy(length, 0, output, checksumLength, sizeIentLength);
		System.arraycopy(new byte[] {command}, 0, output, checksumLength + sizeIentLength, 1);
		System.arraycopy(message, 0, output, checksumLength + sizeIentLength + 1, message.length);

		if (verbose) {
			print("[Message] Generated new checksum: ");
			for (Byte b : checksum)
	  			System.out.print(b.toString() + " ");
	  		System.out.println();
	  		print("[Message] Message Length: ");
			for (Byte b : length)
	  			System.out.print(b.toString() + " ");
	  		System.out.println();

	  		println("[Message] Message Command: " + Integer.toHexString(command));

	  		print("[Message] Sending message: ");
	  		for (Byte b : message)
	  			System.out.print(b.toString() + " ");
	  		System.out.println();
	  	}


  		try {
  			out.write(output);
  		}catch(IOException e) {
  			println("[Message] IOException while sending message");
  			return false;
  		}


  		// Checking the checksum responce
  		if (command==EXIT) {
  			// Do not check for checksum with exit command (to keep server from hanging up)
        	println("[Message] Exit Command. No checksum.");
        	return true;
  		} else {
  			int time = 0;
  			try {
	  			while (in.available() < checksumLength && time < checksumTimeout) {
	  				time += 10;
	  				try {Thread.sleep(10);}catch(Exception e){}
	  			}
	  			if (in.available() >= checksumLength) {
	  				byte[] ret = new byte[checksumLength];
	  				in.read(ret);

	  				if (verbose) {
						print("[Message] Received checksum: ");
						for (Byte b : ret)
				  			System.out.print(b.toString() + " ");
				  		System.out.println();
				  	}

				  	boolean success = true;
				  	for (int i = 0; i < checksumLength && success; i++) {
				  		if (checksum[i] != ret[i])
				  			success = false;
				  	}

	  				if (success) {
	  					println("[Message] Correct checksum received. Success.");
	  					return true;
	  				} else {
	  					println("[Message] Incorrect checksum received. Failure.");
	  					return false;
	  				}
	  			} else {
	  				println("[Message] Checksum timeout reached. Failure.");
	  				return false;
	  			}
	  		} catch (IOException e) {
	  			println("[Message] IOException while waiting for checksum. Failure");
	  			println("[Message] " + e.getMessage());
	  			return false;
	  		}
  		}
    }


    public static byte[] removeCommand(byte[] input) {
    	if (input.length > 0) {
	    	byte[] newArr = new byte[input.length - 1];
	        System.arraycopy(input, 1, newArr, 0, newArr.length);
	        return newArr;
	    } else {
	    	return null;
	    }
    }

	public boolean isOpen() {
		if (clientSocket == null) {
			return false;
		} else {
			if (clientSocket.isClosed())
				return false;
		}
		return true;
    }

    public int available() throws IOException{
    	return in.available();
    }

 //    public int getPort() {
	// 	return portNumber;
	// }

	// public byte[] getPortBytes() {
	// 	return ByteBuffer.allocate(4).putInt(portNumber).array();
	// }

	// public int getRoutingPort() {
	// 	return routingPort;
	// }

	// public byte[] getRoutingPortBytes() {
	// 	return ByteBuffer.allocate(4).putInt(routingPort).array();
	// }

	private String prefix;
	private void println(String str, boolean err) {
		prefix = "[Connection : " + portNumber + "] ";
		if (verbose) {
			if (err) {
				if (str.toLowerCase().equals("error.") || str.toLowerCase().equals("done."))
					System.err.println(str);
				else
					System.err.println(prefix+str);
			}else{
				if (str.toLowerCase().equals("error.") || str.toLowerCase().equals("done."))
					System.out.println(str);
				else
					System.out.println(prefix+str);
			}
		}
	}
	private void println(String str) {
		prefix = "[Connection : " + portNumber + "] ";
		if (verbose) {
			if (str.toLowerCase().equals("error.") || str.toLowerCase().equals("done."))
				System.out.println(str);
			else
				System.out.println(prefix+str);
		}

	}
	private void print(String str, boolean err) {
		prefix = "[Connection : " + portNumber + "] ";
		if (verbose) {
			if (err)
				System.err.print(prefix+str);
			else
				System.out.print(prefix+str);
		}

	}
	private void print(String str) {
		prefix = "[Connection : " + portNumber + "] ";
		if (verbose) {
			System.out.print(prefix+str);
		}

	}
}
