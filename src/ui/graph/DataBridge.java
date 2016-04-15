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
import ui.controllers.GUI;
import ui.controllers.VDriver;
import util.DoubleHolder;
import util.IntegerHolder;

public class DataBridge {
	LineChart<Number, Number> fitnessChart;
	LineChart<Number, Number> actionChart;
	ArrayList<String> fitnessGraphSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> fitnessGraphSeries = FXCollections.observableArrayList();
	
	ArrayList<String> actionGraphSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> actionGraphSeries = FXCollections.observableArrayList();
	EATab eaTab;
	VDriver vDriver;
	final boolean virtual;
	
	/**
	 * Initializes a new real DataBridge, with a LineChart and a parent EATab.
	 * @param fitnessChart The LineChart in the GUI
	 * @param eaTab The EATab tab that holds all of this
	 */
	public DataBridge(LineChart<Number, Number> fitnessChart, LineChart<Number, Number> actionChart, EATab eaTab){
		virtual = false;
		this.eaTab = eaTab;
		this.fitnessChart = fitnessChart;
		this.actionChart = actionChart;
		//EAController.addSeries(graphSeries);
		fitnessChart.setData(fitnessGraphSeries);
		actionChart.setData(actionGraphSeries);
		fitnessChart.setCreateSymbols(false);
		actionChart.setCreateSymbols(false);
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
	
	public void viewGraphs(boolean val){
		if(isVirtual())
			return;
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setGraphView(val);
			}
		});
	}
	
	private synchronized void setGraphView(boolean val){
		if(val){
			fitnessChart.setData(fitnessGraphSeries);
			actionChart.setData(actionGraphSeries);
		}else{
			fitnessChart.setData(null);
			actionChart.setData(null);
		}
	}
	
	//--------------------------Functions----------------------------
	
	//TODO: Implement the check system for goals and stuff
	public void check(ElementHolder elements){
		vDriver.check(elements);
		//new Thread(new GoalChecker(vDriver, elements)).start();
	}
	
	//----------------------------Graph------------------------------
	
	public void setCharts(LineChart<Number, Number> chart1, LineChart<Number, Number> chart2){
		this.fitnessChart = chart1;
		this.actionChart = chart2;
		fitnessChart.setData(fitnessGraphSeries);
		fitnessChart.setCreateSymbols(false);
		actionChart.setData(actionGraphSeries);
		actionChart.setCreateSymbols(false);
	}
	
	//---
	
	/**
	 * Adds a series to the graph.
	 * @param name the name of the series to add
	 */
	public synchronized void addSeries(String name, ChartType t){
		Series<Number, Number> s;
		switch(t){
		case FITNESS:
			fitnessGraphSeriesTitles.add(name);
			s = new Series<Number, Number>();
			s.setName(name);
			fitnessGraphSeries.add(s);
			break;
		case ACTION:
			actionGraphSeriesTitles.add(name);
			s = new Series<Number, Number>();
			s.setName(name);
			actionGraphSeries.add(s);
			break;
		}
	}
	
	/**
	 * Adds data to a series. NOTE: MUST BE RAN IN THE Platform THREAD! 
	 * Use graphData(String series, Number[] data) if unsure about what to do.
	 * @param name the name of the Series to add to
	 * @param vals the values to add to the series, X and Y
	 */
	public synchronized void addToSeries(String name, ChartType t, Number[] vals){
		Data<Number, Number> dat;
		switch(t){
		case FITNESS:
			if(!fitnessGraphSeriesTitles.contains(name)){
				addSeries(name, t);
			}
			dat = new XYChart.Data<Number, Number>(vals[0],vals[1]);
			getSeries(name, t).getData().add(dat);
			break;
		case ACTION:
			if(!actionGraphSeriesTitles.contains(name)){
				addSeries(name, t);
			}
			dat = new XYChart.Data<Number, Number>(vals[0],vals[1]);
			getSeries(name, t).getData().add(dat);
			break;
		}
		
	}
	
	/**
	 * Gets a Series via an index.
	 * @param index the index of the series
	 * @return the Series at the index
	 */
	public synchronized Series<Number, Number> getSeries(int index, ChartType t){
		switch(t){
		case FITNESS:
			return fitnessGraphSeries.get(index);
		case ACTION:
			return actionGraphSeries.get(index);
		default:
			return null;
		}
		
	}
	
	/**
	 * Gets a Series by the name provided.
	 * @param name the name of the Series to return
	 * @return the Series
	 */
	public Series<Number,Number> getSeries(String name, ChartType t){
		switch(t){
		case FITNESS:
			for(int i = 0; i < fitnessGraphSeriesTitles.size(); i++){
				if(fitnessGraphSeriesTitles.get(i).equals(name))
					return fitnessGraphSeries.get(i);
			}
			break;
		case ACTION:
			for(int i = 0; i < actionGraphSeriesTitles.size(); i++){
				if(actionGraphSeriesTitles.get(i).equals(name))
					return actionGraphSeries.get(i);
			}
			break;
		}
		return null;
	}
	
	/**
	 * Removes the a Series.
	 * @param name the name of the Series to remove
	 */
	public synchronized void removeSeries(String name, ChartType t){
		switch(t){
		case FITNESS:
			for(int i = 0; i < fitnessGraphSeriesTitles.size(); i++){
				if(fitnessGraphSeriesTitles.get(i).equals(name)){
					fitnessGraphSeriesTitles.remove(i);
					fitnessGraphSeries.remove(i);
				}
			}
			break;
		case ACTION:
			for(int i = 0; i < actionGraphSeriesTitles.size(); i++){
				if(actionGraphSeriesTitles.get(i).equals(name)){
					actionGraphSeriesTitles.remove(i);
					actionGraphSeries.remove(i);
				}
			}
			break;
		}
		
	}
	
	//---
	
	/**
	 * Graphs data to a series. NOTE: the Number[] data array needs to be 2 elements long, X and Y.
	 * @param series the series to add the data to
	 * @param data the data to add to the graph
	 */
	public synchronized void graphData(String series, ChartType t, Number[] data){
		if(!virtual){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					addToSeries(series, t, data);
				}
			});
		}else
			addToSeries(series, t, data);
	}
	
	/**
	 * Resets ALL graph data associated with the DataBridge 
	 */
	public synchronized void resetGraph(ChartType t){
		switch(t){
		case FITNESS:
			clearAllData(t);
			fitnessGraphSeries.clear();
			fitnessGraphSeriesTitles.clear();
			break;
		case ACTION:
			clearAllData(t);
			actionGraphSeries.clear();
			actionGraphSeriesTitles.clear();
			break;
		}
		
	}

	private void clearAllData(ChartType t){
		switch(t){
		case FITNESS:
			for(Series<Number, Number> s : fitnessGraphSeries){
				s.getData().clear();
			}
			break;
		case ACTION:
			for(Series<Number, Number> s : actionGraphSeries){
				s.getData().clear();
			}
			break;
		}
	}

	/**
	 * Clears the data in a particular series on the graph.
	 * @param name the name of the series to clear
	 */
	public synchronized void clearSeriesData(String name, ChartType t){
		getSeries(name,t).getData().clear();
	}
	
	/**
	 * Simplifies the graph data in a series, using the number of points to average.
	 * @param seriesName the name of the series to simplify
	 * @param averageFrames the number of frames to average together in groups
	 */
	public synchronized void simplifyData(String seriesName, ChartType t, int averageFrames){
		//TODO: Make this not suck 
		ObservableList<XYChart.Data<Number, Number>> seriesData = getSeries(seriesName, t).getData();
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
		
		getSeries(seriesName, t).getData().clear();
		getSeries(seriesName, t).getData().addAll(newSeriesData);
		newSeriesData = null;
	}
	
	/**
	 * Gets the size of a series for this DataBridge
	 * @param name the name of the series to check
	 * @return the size of the series in question
	 */
	public int getGraphSize(String name, ChartType t){
		if(getSeries(name, t) != null)
			return getSeries(name, t).getData().size();
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
					eaTab.progressBar.setTetherProgress(d, generateTimeRemainingLabel(d, timeAtStart));
					//eaTab.progressLabel.setText(generateTimeRemainingLabel(d, timeAtStart));
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
					eaTab.progressBar.setTetherProgress(d);
					eaTab.progressLabel.setText(generateTimeRemainingLabel(d, -1));
				}
			});
		}
	}
	
	/**
	 * Sets the generation tag to the generation provided.
	 * @param g the generation to set the tag to
	 */
	public IntegerHolder genHolder = new IntegerHolder(0);
	long lastGenUpdate = -1;
	public void setGeneration(int g){
		if(!virtual){
			if(lastGenUpdate == -1)
				lastGenUpdate = System.nanoTime();
			if(System.nanoTime() - lastGenUpdate > 83333333){
				lastGenUpdate = System.nanoTime();
			
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						eaTab.setGeneration(g);
						genHolder.setValue(g);
					}
				});
			}
		}
	}
	
	long lastFitnessUpdate = -1;
	public DoubleHolder fitHolder = new DoubleHolder(0);
	public void setFitness(double d){
		if(!virtual){
			if(lastFitnessUpdate == -1)
				lastFitnessUpdate = System.nanoTime();
			if(System.nanoTime() - lastFitnessUpdate > 83333333){
				lastFitnessUpdate = System.nanoTime();
			
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						eaTab.setFitness(d);
						fitHolder.setValue(d);
					}
				});
			}
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
	
	public enum ChartType{
		ACTION, FITNESS;
	}
	
	//--------------------------File IO------------------------------
	
	/**
	 * Writes this DataBridge's data to the file specified.
	 * @param f the File to write to
	 * @throws IOException
	 */
	public void writeData(File f, ChartType t) throws IOException{
		System.out.print(f.getAbsolutePath() + "\t" + t);
		PrintWriter out = new PrintWriter(f);
		ArrayList<String> array;
		switch(t){
		case FITNESS:
			array = fitnessGraphSeriesTitles;
			break;
		case ACTION:
			array = actionGraphSeriesTitles;
			break;
		default:
			out.close();
			return;
		}
		for(String s : array){
			out.println(s);
			//TODO: Make this actually get data from the axis
			out.println("Generation, Value");
			ObservableList<Data<Number, Number>> series = getSeries(s, t).getData();
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
	public byte[][][] streamData(){
		byte[][][] out = new byte[2][fitnessGraphSeriesTitles.size()][];
		for(int i = 0; i < fitnessGraphSeriesTitles.size(); i++){
			String s = fitnessGraphSeriesTitles.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(s + "\n");
			// TODO: Add axis support here
			sb.append("Generation, Val\n");
			ObservableList<Data<Number, Number>> series = getSeries(s, ChartType.FITNESS).getData();
			for(Data<Number, Number> dat : series){
				sb.append(dat.getXValue() + "," + dat.getYValue() + "\n");
			}
			out[0][i] = sb.toString().getBytes();
		}
		
		for(int i = 0; i < actionGraphSeriesTitles.size(); i++){
			String s = actionGraphSeriesTitles.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(s + "\n");
			// TODO: Add axis support here
			sb.append("Generation, Val\n");
			ObservableList<Data<Number, Number>> series = getSeries(s, ChartType.ACTION).getData();
			for(Data<Number, Number> dat : series){
				sb.append(dat.getXValue() + "," + dat.getYValue() + "\n");
			}
			out[1][i] = sb.toString().getBytes();
		}
		return out;
	}
}



/*

switch(t){
case FITNESS:
	break;
case ACTION:
	break;
}

*/