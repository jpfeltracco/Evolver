package ui.controllers;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformManagedObject;
import java.util.ArrayList;

import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.Attribute;
import javax.management.ObjectName;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.NumberAxis;

public class CPU implements Runnable{
	private final AreaChart<Number,Number> memoryChart;
	private final AreaChart<Number,Number> cpuChart;
	private final AreaChart<Number,Number> threadChart;
	private final AreaChart<Number,Number> vDriverChart;
	
	ArrayList<String> threadSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> threadSeries = FXCollections.observableArrayList();
	
	ArrayList<String> cpuSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> cpuSeries = FXCollections.observableArrayList();
	
	ArrayList<String> memorySeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> memorySeries = FXCollections.observableArrayList();
	
	ArrayList<String> vDriverSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> vDriverSeries = FXCollections.observableArrayList();
	
	static final int MAXSIZE = 100;
	static final int WAITTIME = 1000;
	
	public CPU(AreaChart<Number,Number> memoryChart, AreaChart<Number,Number> cpuChart, AreaChart<Number,Number> threadChart, AreaChart<Number,Number> vDriverChart){
		this.memoryChart = memoryChart;
		this.cpuChart = cpuChart;
		this.threadChart = threadChart;
		this.vDriverChart = vDriverChart;
		//------
		
		Series<Number, Number> s = new Series<Number, Number>();
		cpuSeriesTitles.add("CPU Usage");
		s.setName("CPU Usage");
		cpuSeries.add(s);
		
		//------
		
		s = new Series<Number, Number>();
		memorySeriesTitles.add("Max Heap");
		s.setName("Max Heap");
		memorySeries.add(s);
		
		s = new Series<Number, Number>();
		memorySeriesTitles.add("Used Heap");
		s.setName("Used Heap");
		memorySeries.add(s);
		
		//------
		
		s = new Series<Number, Number>();
		threadSeriesTitles.add("Peak Thread Count");
		s.setName("Peak Thread Count");
		threadSeries.add(s);
		
		s = new Series<Number, Number>();
		threadSeriesTitles.add("Thread Count");
		s.setName("Thread Count");
		threadSeries.add(s);
		
		//------
		

		s = new Series<Number, Number>();
		vDriverSeriesTitles.add("Number of VDrivers");
		s.setName("Number of VDrivers");
		vDriverSeries.add(s);
		
		//------
		this.memoryChart.setData(memorySeries);
		this.memoryChart.setCreateSymbols(false);
		this.memoryChart.getXAxis().autoRangingProperty().set(false);
		((NumberAxis)this.memoryChart.getXAxis()).setUpperBound(MAXSIZE + WAITTIME/1000.0);
		
		//------
		
		this.threadChart.setData(threadSeries);
		this.threadChart.setCreateSymbols(false);
		this.threadChart.getXAxis().autoRangingProperty().set(false);
		((NumberAxis)this.threadChart.getXAxis()).setUpperBound(MAXSIZE + WAITTIME/1000.0);
		
		//------
		
		this.cpuChart.setData(cpuSeries);
		this.cpuChart.setCreateSymbols(false);
		this.cpuChart.getXAxis().autoRangingProperty().set(false);
		this.cpuChart.getYAxis().autoRangingProperty().set(false);
		//this.cpuChart.getXAxis().setTickLength(20);
		//this.cpuChart.getYAxis().setTickLength(20);
		((NumberAxis)this.cpuChart.getYAxis()).setUpperBound(100);
		((NumberAxis)this.cpuChart.getXAxis()).setUpperBound(MAXSIZE + WAITTIME/1000.0);
		
		//------
		
		this.vDriverChart.setData(vDriverSeries);
		this.vDriverChart.setCreateSymbols(false);
		this.vDriverChart.getXAxis().autoRangingProperty().set(false);
		((NumberAxis)this.vDriverChart.getYAxis()).setUpperBound(5);
		((NumberAxis)this.vDriverChart.getXAxis()).setUpperBound(MAXSIZE + WAITTIME/1000.0);
		
	}
	
	long nanoTime;
	
	@Override
	public void run() {
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		nanoTime = System.nanoTime();
		
		while(GUI.running){
			try {
				MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
			    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
			    AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

			    
			    OperatingSystemMXBean po = ManagementFactory.getOperatingSystemMXBean();
			    MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
			    ThreadMXBean thread = ManagementFactory.getThreadMXBean();
			    
			    double timeSec = (System.nanoTime() - nanoTime)/1000000000.0;
			    if (!list.isEmpty()){

				    Attribute att = (Attribute)list.get(0);
				    Double value  = (Double)att.getValue();
	
				    // usually takes a couple of seconds before we get real values
				    if (value != -1.0){
				    // returns a percentage value with 1 decimal point precision
				    	addToSystemGraph("CPU Usage", cpuChart, timeSec, ((int)(value * 1000) / 10.0));
				    	//System.out.println("CPU: " + ((int)(value * 1000) / 10.0));
				    }
			    }
			    
			    //System.out.println("Processers: " + po.getAvailableProcessors());
			    //System.out.println("Arch: " + po.getArch());
			    //System.out.println("Heap: " + mem.getHeapMemoryUsage());
			    
			    
			    
			    /*Data<Number, Number> dat = new XYChart.Data<Number, Number>(timeSec,vals[1]);
				getSeries(name).getData().add(dat);*/
				
			    addToSystemGraph("Thread Count", threadChart, timeSec, thread.getThreadCount());
			    addToSystemGraph("Peak Thread Count", threadChart, timeSec, thread.getPeakThreadCount());
				
			    addToSystemGraph("Max Heap", memoryChart, timeSec, (double)mem.getHeapMemoryUsage().getCommitted() / 1048576.0);
			    addToSystemGraph("Used Heap", memoryChart, timeSec, (double)mem.getHeapMemoryUsage().getUsed() / 1048576.0);
			    
			    addToSystemGraph("Number of VDrivers", vDriverChart, timeSec, VDriver.getNumVDrivers());
			    
			    
			    
			    
				Thread.sleep(WAITTIME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void addToSystemGraph(String series, AreaChart<Number,Number> chart, Number data1, Number data2){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Data<Number, Number> dat = new XYChart.Data<Number, Number>(data1, data2);
				ObservableList<Data<Number, Number>> dataHolder = getSeries(series, chart).getData();
				dataHolder.add(dat);
				if((double)data1 > ((NumberAxis)chart.getXAxis()).getUpperBound())
					((NumberAxis)chart.getXAxis()).setUpperBound((double)data1);
				
				if(dataHolder.size() > MAXSIZE){
					dataHolder.remove(0);
					((NumberAxis)chart.getXAxis()).setLowerBound((double)dataHolder.get(0).getXValue());
				}
				
			}
		});
	}
	
	/**
	 * Gets a Series by the name provided.
	 * @param name the name of the Series to return
	 * @return the Series
	 */
	public Series<Number,Number> getSeries(String name, AreaChart<Number,Number> chart){
		switch(chart.getTitle()){
		case "Memory":
			for(int i = 0; i < memorySeriesTitles.size(); i++){
				if(memorySeriesTitles.get(i).equals(name))
					return chart.getData().get(i);
			}
			break;
		case"CPU":
			for(int i = 0; i < cpuSeriesTitles.size(); i++){
				if(cpuSeriesTitles.get(i).equals(name))
					return chart.getData().get(i);
			}
			break;
		case"Threads":
			for(int i = 0; i < threadSeriesTitles.size(); i++){
				if(threadSeriesTitles.get(i).equals(name))
					return chart.getData().get(i);
			}
			break;
		case"Virtual Tab Data":
		for(int i = 0; i < vDriverSeriesTitles.size(); i++){
			if(vDriverSeriesTitles.get(i).equals(name))
				return chart.getData().get(i);
		}
		break;
	}
		
		return null;
	}
	
}
