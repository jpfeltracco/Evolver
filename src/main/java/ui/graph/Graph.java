package ui.graph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import ui.controllers.EATab;

public class Graph {
	LineChart<Number, Number> chart;
	ArrayList<String> graphSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> graphSeries = FXCollections.observableArrayList();
	EATab EAController;
	boolean setLoaded = false;
	public Graph(LineChart<Number, Number> chart, EATab EAController){
		this.EAController = EAController;
		this.chart = chart;
		//EAController.addSeries(graphSeries);
		System.out.println("Thing: " + chart);
		chart.setData(graphSeries);
		chart.setCreateSymbols(false);
	}
	
	public void graphData(String series, Number[] data){
		new GraphData(this, series, data);
	}
	
	public void setLoaded(boolean val){
		setLoaded = val;
	}
	
	public boolean getLoaded(){
		return setLoaded;
	}
	
	public synchronized void addSeries(String name){
		graphSeriesTitles.add(name);
		Series<Number, Number> s = new Series<Number, Number>();
		s.setName(name);
		//System.out.println(s.getData().);
		
		graphSeries.add(s);
	}
	
	public synchronized void addToSeries(String name, Number[] vals){
		if(!graphSeriesTitles.contains(name)){
			addSeries(name);
		}
		Data<Number, Number> dat = new XYChart.Data<Number, Number>(vals[0],vals[1]);
		getSeries(name).getData().add(dat);
	}
	
	private Series<Number,Number> getSeries(String name){
		for(int i = 0; i < graphSeriesTitles.size(); i++){
			if(graphSeriesTitles.get(i).equals(name))
				return graphSeries.get(i);
		}
		return null;
	}
	
	public int size(String name){
		if(getSeries(name) != null)
			return getSeries(name).getData().size();
		return 0;
	}
	
	public synchronized void removeSeries(String name){
		for(int i = 0; i < graphSeriesTitles.size(); i++){
			if(graphSeriesTitles.get(i).equals(name)){
				graphSeriesTitles.remove(i);
				graphSeries.remove(i);
			}
		}
	}
	
	public synchronized void resetGraph(){
		clearAllData();
		graphSeries.clear();
		graphSeriesTitles.clear();
	}

	public synchronized void clearSeriesData(String name){
		getSeries(name).getData().clear();
	}
	
	public synchronized void clearAllData(){
		for(Series<Number, Number> s : graphSeries){
			s.getData().clear();
		}
	}
	
	public synchronized void simplifyData(String seriesName, int averageFrames){
		ObservableList<XYChart.Data<Number, Number>> seriesData = getSeries(seriesName).getData();
		ObservableList<XYChart.Data<Number, Number>> newSeriesData = FXCollections.observableArrayList();
		
		/*for(int i = 0; i < startFrom; i++){
			newSeriesData.add(seriesData.get(i));
			System.out.println("Same Data");
		}*/
		
		
		for(int i = 0; i < seriesData.size(); i+= averageFrames){
			double avg = 0;
			if(i + averageFrames > seriesData.size())
				break;
			//System.out.println("New Avg");
			for(int j = 0; j < averageFrames; j++){
				avg += seriesData.get(i+j).getYValue().doubleValue();
				//System.out.print(".");
			}
			newSeriesData.add(new XYChart.Data<Number,Number>( seriesData.get(i+averageFrames-1).getXValue().intValue(), avg/((double)averageFrames)));
		}

		getSeries(seriesName).getData().clear();
		getSeries(seriesName).getData().addAll(newSeriesData);
		newSeriesData = null;
		
	}
	
	public void setGeneration(int g){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				EAController.setGeneration(g);
			}
		});
	}
	
	public void writeData(File f) throws IOException{
		new File(f+"/output/").mkdir();
		
		System.out.print(f.getAbsolutePath());
		for(String s : graphSeriesTitles){
			PrintWriter out = new PrintWriter(f.getAbsolutePath() + "/output/" + s + ".txt");
			out.println("Generation,Fitness");
			ObservableList<Data<Number, Number>> series = getSeries(s).getData();
			for(Data<Number, Number> dat : series){
				out.println(dat.getXValue() + "," + dat.getYValue());
			}
			out.close();
		}
	}
	
	public byte[][] streamData(){
		byte[][] out = new byte[graphSeriesTitles.size()][];
		for(int i = 0; i < graphSeriesTitles.size(); i++){
			String s = graphSeriesTitles.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(s + "\n");
			// TODO: Add axis support here
			sb.append("Generation,Fitness\n");
			ObservableList<Data<Number, Number>> series = getSeries(s).getData();
			for(Data<Number, Number> dat : series){
				sb.append(dat.getXValue() + "," + dat.getYValue() + "\n");
			}
			out[i] = sb.toString().getBytes();
		}
		return out;
	}
	

}
