package ui.graph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import evolver.ElementHolder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import ui.controllers.EATab;
import ui.controllers.VDriver;

public class DataBridge {
	LineChart<Number, Number> chart;
	ArrayList<String> graphSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> graphSeries = FXCollections.observableArrayList();
	EATab eaTab;
	VDriver vDriver;
	final boolean virtual;
	
	/**
	 * Initializes a new real DataBridge, with a LineChart and a parent EATab.
	 * @param chart The LineChart in the GUI
	 * @param eaTab The EATab tab that holds all of this
	 */
	public DataBridge(LineChart<Number, Number> chart, EATab eaTab){
		virtual = false;
		this.eaTab = eaTab;
		this.chart = chart;
		//EAController.addSeries(graphSeries);
		System.out.println("Thing: " + chart);
		chart.setData(graphSeries);
		chart.setCreateSymbols(false);
	}
	
	/**
	 * Initializes a new virtual DataBridge.
	 * @param vDriver The VDriver that holds all of this
	 */
	public DataBridge(VDriver vDriver){
		virtual = true;
		this.vDriver = vDriver;
	}
	
	/**
	 * Checks whether this DataBridge represents a virtual system or not.
	 * @return if this DataBridge is virtual
	 */
	public boolean isVirtual(){
		return virtual;
	}
	
	//--------------------------Functions----------------------------
	
	//TODO: Implement the check system for goals and stuff
	public void check(ElementHolder elements){
		vDriver.check(elements);
		//new Thread(new GoalChecker(vDriver, elements)).start();
	}
	
	//----------------------------Graph------------------------------
	
	public void setChart(LineChart<Number, Number> chart){
		this.chart = chart;
		chart.setData(graphSeries);
		chart.setCreateSymbols(false);
	}
	
	//---
	
	/**
	 * Adds a series to the graph.
	 * @param name the name of the series to add
	 */
	public synchronized void addSeries(String name){
		graphSeriesTitles.add(name);
		Series<Number, Number> s = new Series<Number, Number>();
		s.setName(name);
		graphSeries.add(s);
	}
	
	/**
	 * Adds data to a series. NOTE: MUST BE RAN IN THE Platform THREAD! 
	 * Use graphData(String series, Number[] data) if unsure about what to do.
	 * @param name the name of the Series to add to
	 * @param vals the values to add to the series, X and Y
	 */
	public synchronized void addToSeries(String name, Number[] vals){
		if(!graphSeriesTitles.contains(name)){
			addSeries(name);
		}
		Data<Number, Number> dat = new XYChart.Data<Number, Number>(vals[0],vals[1]);
		getSeries(name).getData().add(dat);
	}
	
	/**
	 * Gets a Series via an index.
	 * @param index the index of the series
	 * @return the Series at the index
	 */
	public synchronized Series<Number, Number> getSeries(int index){
		return graphSeries.get(index);
	}
	
	/**
	 * Gets a Series by the name provided.
	 * @param name the name of the Series to return
	 * @return the Series
	 */
	public Series<Number,Number> getSeries(String name){
		for(int i = 0; i < graphSeriesTitles.size(); i++){
			if(graphSeriesTitles.get(i).equals(name))
				return graphSeries.get(i);
		}
		return null;
	}
	
	/**
	 * Removes the a Series.
	 * @param name the name of the Series to remove
	 */
	public synchronized void removeSeries(String name){
		for(int i = 0; i < graphSeriesTitles.size(); i++){
			if(graphSeriesTitles.get(i).equals(name)){
				graphSeriesTitles.remove(i);
				graphSeries.remove(i);
			}
		}
	}
	
	//---
	
	/**
	 * Graphs data to a series. NOTE: the Number[] data array needs to be 2 elements long, X and Y.
	 * @param series the series to add the data to
	 * @param data the data to add to the graph
	 */
	public synchronized void graphData(String series, Number[] data){
		if(!virtual){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					addToSeries(series, data);
				}
			});
		}else
			addToSeries(series, data);
	}
	
	/**
	 * Resets ALL graph data associated with the DataBridge 
	 */
	public synchronized void resetGraph(){
		clearAllData();
		graphSeries.clear();
		graphSeriesTitles.clear();
	}

	private void clearAllData(){
		for(Series<Number, Number> s : graphSeries){
			s.getData().clear();
		}
	}

	/**
	 * Clears the data in a particular series on the graph.
	 * @param name the name of the series to clear
	 */
	public synchronized void clearSeriesData(String name){
		getSeries(name).getData().clear();
	}
	
	/**
	 * Simplifies the graph data in a series, using the number of points to average.
	 * @param seriesName the name of the series to simplify
	 * @param averageFrames the number of frames to average together in groups
	 */
	public synchronized void simplifyData(String seriesName, int averageFrames){
		//TODO: Make this not suck 
		ObservableList<XYChart.Data<Number, Number>> seriesData = getSeries(seriesName).getData();
		ObservableList<XYChart.Data<Number, Number>> newSeriesData = FXCollections.observableArrayList();		
		
		for(int i = 0; i < seriesData.size(); i+= averageFrames){
			double avg = 0;
			if(i + averageFrames > seriesData.size())
				break;
			for(int j = 0; j < averageFrames; j++){
				avg += seriesData.get(i+j).getYValue().doubleValue();
			}
			newSeriesData.add(new XYChart.Data<Number,Number>( seriesData.get(i+averageFrames-1).getXValue().intValue(), avg/((double)averageFrames)));
		}

		getSeries(seriesName).getData().clear();
		getSeries(seriesName).getData().addAll(newSeriesData);
		newSeriesData = null;
	}
	
	/**
	 * Gets the size of a series for this DataBridge
	 * @param name the name of the series to check
	 * @return the size of the series in question
	 */
	public int getGraphSize(String name){
		if(getSeries(name) != null)
			return getSeries(name).getData().size();
		return 0;
	}
	
	//--------------------------Update Widgets----------------------------

	/**
	 * Sets the progress bar to a percent provided. Also updates the time remaining
	 * label. NOTE: long timeAtStart should be gotten via System.nanoTime(). Also 
	 * only does anything if this DataBridge isn't virtual. 
	 * @param d the percentage complete
	 * @param timeAtStart the time since start. (Via System.nanoTime())
	 */
	public void setProgress(double d, long timeAtStart){
		if(!virtual){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					eaTab.progressBar.setProgress(d);
					eaTab.progressLabel.setText(generateTimeRemainingLabel(d, timeAtStart));
				}
			});
		}
	}
	
	/**
	 * Sets the progress bar to a percent provided. NOTE: Does not update the
	 * time remaining label. (Automatically set to the default value) Also 
	 * only does anything if this DataBridge isn't virtual. 
	 * @param d the percentage complete
	 */
	public void setProgress(double d){
		if(!virtual){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					eaTab.progressBar.setProgress(d);
					eaTab.progressLabel.setText(generateTimeRemainingLabel(d, -1));
				}
			});
		}
	}
	
	/**
	 * Sets the generation tag to the generation provided.
	 * @param g the generation to set the tag to
	 */
	public void setGeneration(int g){
		if(!virtual){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					eaTab.setGeneration(g);
				}
			});
		}
	}
	
	private String generateTimeRemainingLabel(double progressPercentage, long timeAtStart) {
		 if(timeAtStart == -1)
			 return "--";
		 long elapsedTime = System.nanoTime() - timeAtStart;
		 double secRemaining = (double)elapsedTime / progressPercentage / 1000000000.0 - (double)elapsedTime / 1000000000.0;
		 String out = (int)(progressPercentage*100) + "%, est " + (int)secRemaining + "s";
		 return out;
	}
	
	//--------------------------File IO------------------------------
	
	/**
	 * Writes this DataBridge's data to the file specified.
	 * @param f the File to write to
	 * @throws IOException
	 */
	public void writeData(File f) throws IOException{
		System.out.print(f.getAbsolutePath());
		PrintWriter out = new PrintWriter(f);
		for(String s : graphSeriesTitles){
			out.println(s);
			out.println("Generation,Value");
			ObservableList<Data<Number, Number>> series = getSeries(s).getData();
			for(Data<Number, Number> dat : series){
				out.println(dat.getXValue() + "," + dat.getYValue());
			}
			out.println("");
		}
		out.close();
	}
	
	/**
	 * Streams this DataBridge's data as a byte[][] array. Useful for Serialization.
	 * @return a byte[][] array version of this DataBridge
	 */
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