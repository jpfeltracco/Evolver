package ui.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Terminal {
	
	public Terminal(){
		PrintStream origOut = System.out;
    	Interceptor interceptor = new Interceptor(origOut);
        System.setOut(interceptor);// just add the interceptor
        //System.setIn();
	}
	
	
	private class Interceptor extends PrintStream
	{
	    public Interceptor(OutputStream out)
	    {
	        super(out, true);
	    }
	    @Override
	    public void print(String s)
	    {//do what ever you like
	    	super.print("lol");
	        //super.print(s);
	    }
	    
	    @Override
	    public void print(boolean b)
	    {//do what ever you like
	    	super.print("lol Boolean");
	        //super.print(s);
	    }
	}
	
	private class Sender extends InputStream
	{
	    public Sender(){
	    	
	    }

		@Override
		public int read() throws IOException {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
}
