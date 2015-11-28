package ui.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Console {
	
	Interceptor interceptor;
	Reader reader;
	
	public Console(){
		PrintStream origOut = System.out;
    	interceptor = new Interceptor(origOut);
    	reader = new Reader("This is a test!" + System.lineSeparator());
        System.setOut(interceptor);// just add the interceptor
        System.setIn(reader);
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
	    	super.print("[Console] " + s);
	        //super.print(s);
	    }
	    
	    @Override
	    public void print(boolean b)
	    {//do what ever you like
	    	super.print("[Console] " + b);
	        //super.print(s);
	    }
	    
	    @Override
	    public void print(Object o)
	    {//do what ever you like
	    	super.print("[Console] " + o);
	        //super.print(s);
	    }
	}
	
	private class Reader extends InputStream
	{
		String msg;
	    public Reader(String msg){
	    	this.msg = msg;
	    }

		@Override
		public int read() throws IOException {
			
			if(msg.length()==0)
				return -1;
			
			char c = msg.substring(0,1).charAt(0);
			int i = (int)((new Character(c))).charValue();
			msg = msg.substring(1);
			return i;
		}
	}
	
}
