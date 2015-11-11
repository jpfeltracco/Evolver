package ui.graph;

import javafx.application.Platform;

public class GraphData implements Runnable{
	private final DataBridge graph;
	private final Number[] data;
	private final String series;
	
	public GraphData(DataBridge graph, String series, Number[] data){
		this.graph = graph;
		this.data = data;
		this.series = series;
		if(!graph.isVirtual())
			Platform.runLater(this);
		else
			graph.addToSeries(series, data);
	}

	@Override
	public void run() {
		graph.addToSeries(series, data);
	}
}