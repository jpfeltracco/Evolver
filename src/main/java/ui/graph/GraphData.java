package ui.graph;

import javafx.application.Platform;

public class GraphData implements Runnable{
	private final Graph graph;
	private final Number[] data;
	private final String series;
	
	public GraphData(Graph graph, String series, Number[] data){
		this.graph = graph;
		this.data = data;
		this.series = series;
		Platform.runLater(this);
	}

	@Override
	public void run() {
		graph.addToSeries(series, data);
	}
}