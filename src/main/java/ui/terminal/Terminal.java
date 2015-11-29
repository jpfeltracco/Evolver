package ui.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.Buffer;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ui.controllers.FXController;
import ui.controllers.GUI;

public class Terminal {
	
	TextField field;
	
	byte[] inputText;
	Sender sender;
	Interceptor interceptor;
	
	public static final byte[] NEWLINE = "\n".getBytes();
	
	public Terminal(TextField field, Interceptor interceptor){
		

		
		this.field = field;
		//PrintStream origOut = System.out;
    	this.interceptor = interceptor;
    	sender = new Sender();
    	
    	field.setOnAction((event) -> {
    		new Thread(() -> {
    			if(field.getText().length() == 0)
    				return;
   				interceptor.print("System.in", field.getText());
    			try {
					addBuffer((field.getText() + "\n").getBytes("UTF-8"));
				} catch (Exception e) {
					e.printStackTrace();
				}
    			Platform.runLater(() ->{
    				field.setText("");
    			});
        	}).start();
    	});
    	
    	
    	
        System.setOut(interceptor);
        
        //System.setErr(interceptor);
        
        System.setIn(sender);
	}
	
	boolean text = true;
	private void addBuffer(byte[] in){
		sender.stop();
		text = true;
		if(inputText == null || inputText.length == 0){
			inputText = in.clone();
		}else{
			byte[] tmp = inputText.clone();
			
			inputText = new byte[tmp.length + in.length];
			
			for(int i = 0; i < tmp.length; i++){
				inputText[i] = tmp[i];
			}
			for(int i = tmp.length; i < in.length; i++){
				inputText[i] = in[i - tmp.length];
			}
		}
		sender.start();
	}
	
	private byte getFromBuffer(){
		sender.stop();
		if(!text || inputText == null){
			//System.out.println("No Text Left");
			return -1;
		}
		if(inputText.length == 0 || inputText[0] == '\0'){
			sender.start();
			inputText = null;
			text = false;
			return 0;
		}
		
		
		byte out = (byte) inputText[0];
		byte[] tmp = inputText.clone();
		inputText = new byte[tmp.length-1];
		for(int i = 1; i < inputText.length; i++){
			inputText[i-1] = tmp[i];
		}
		sender.start();
		text = true;
		return out;
		
	}
	
	
	
	
	private class Sender extends InputStream
	{
		boolean stop = false;
	    public Sender(){
	    	
	    }
	    
	    public synchronized boolean stop(){
	    	stop = true;
	    	return true;
	    }
	    
	    public synchronized boolean start(){
	    	stop = false;
	    	return true;
	    }

		@Override
		public int read() throws IOException {
			if(stop){
				//System.out.print("stopped...");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//return read(inputText, 0, inputText.length);
			
			byte out = getFromBuffer();
			
			if(out == 0){
				return 0;
			}
			while(out == -1 && GUI.running){
				//System.out.print("waiting...");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				out = getFromBuffer();
			}
			//System.out.println("done: " + out);
			return out;
		}
	}
	
}
