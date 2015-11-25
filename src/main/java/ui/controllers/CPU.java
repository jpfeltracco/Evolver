package ui.controllers;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
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
	private final AreaChart<Number,Number> chart;
	ArrayList<String> graphSeriesTitles = new ArrayList<String>();
	ObservableList<Series<Number, Number>> graphSeries = FXCollections.observableArrayList();
	static final int MAXSIZE = 150;
	static final int WAITTIME = 1000;
	
	public CPU(AreaChart<Number,Number> chart){
		this.chart = chart;
		
		Series<Number, Number> s = new Series<Number, Number>();
		graphSeriesTitles.add("CPU Usage");
		s.setName("CPU Usage");
		graphSeries.add(s);
		
		s = new Series<Number, Number>();
		graphSeriesTitles.add("Max Heap");
		s.setName("Max Heap");
		graphSeries.add(s);
		
		s = new Series<Number, Number>();
		graphSeriesTitles.add("Used Heap");
		s.setName("Used Heap");
		graphSeries.add(s);
		
		this.chart.setData(graphSeries);
		this.chart.setCreateSymbols(true);
		this.chart.getXAxis().autoRangingProperty().set(false);
		((NumberAxis)this.chart.getXAxis()).setUpperBound(MAXSIZE + WAITTIME/1000.0);
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
			    
			    double timeSec = (System.nanoTime() - nanoTime)/1000000000.0;
			    if (!list.isEmpty()){

				    Attribute att = (Attribute)list.get(0);
				    Double value  = (Double)att.getValue();
	
				    // usually takes a couple of seconds before we get real values
				    if (value != -1.0){
				    // returns a percentage value with 1 decimal point precision
				    	addToSystemGraph("CPU Usage", timeSec, ((int)(value * 1000) / 10.0));
				    	//System.out.println("CPU: " + ((int)(value * 1000) / 10.0));
				    }
			    }
			    
			    //System.out.println("Processers: " + po.getAvailableProcessors());
			    //System.out.println("Arch: " + po.getArch());
			    //System.out.println("Heap: " + mem.getHeapMemoryUsage());
			    
			    
			    
			    /*Data<Number, Number> dat = new XYChart.Data<Number, Number>(timeSec,vals[1]);
				getSeries(name).getData().add(dat);*/
				
			   
				
			    addToSystemGraph("Max Heap", timeSec, (double)mem.getHeapMemoryUsage().getCommitted() / 1048576.0);
			    addToSystemGraph("Used Heap", timeSec, (double)mem.getHeapMemoryUsage().getUsed() / 1048576.0);
			    
			    
			    
			    
				Thread.sleep(WAITTIME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void addToSystemGraph(String series, Number data1, Number data2){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Data<Number, Number> dat = new XYChart.Data<Number, Number>(data1, data2);
				ObservableList<Data<Number, Number>> dataHolder = getSeries(series).getData();
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
	public Series<Number,Number> getSeries(String name){
		for(int i = 0; i < graphSeriesTitles.size(); i++){
			if(graphSeriesTitles.get(i).equals(name))
				return graphSeries.get(i);
		}
		return null;
	}
	
}
