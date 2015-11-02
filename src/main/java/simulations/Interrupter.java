package simulations;

public class Interrupter implements Runnable{

	private final Thread t;
	public Interrupter(Thread sim){
		t = sim;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.interrupt();
	}

}
