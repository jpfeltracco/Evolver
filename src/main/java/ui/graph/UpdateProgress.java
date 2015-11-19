package ui.graph;

import javafx.application.Platform;

public class UpdateProgress implements Runnable{
	private final double val;
	private final DataBridge dataBridge;
	private final long timeAtStart;
	public UpdateProgress(DataBridge dataBridge, double val, long timeAtStart){
		this.val = val;
		this.dataBridge = dataBridge;
		this.timeAtStart = timeAtStart;
		if(!dataBridge.isVirtual())
			Platform.runLater(this);
	}

	@Override
	public void run() {
		dataBridge.setProgress(val, timeAtStart);
	}
}