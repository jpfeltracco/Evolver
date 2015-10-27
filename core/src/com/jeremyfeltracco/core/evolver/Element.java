package com.jeremyfeltracco.core.evolver;

public class Element implements Comparable<Element> {
	public double config[];
	private double fitness = 0;
	private int gamesPlayed = 0;
	private static int numElements = 0; 
	public int id;
	
	public Element(){
		id = numElements++;
	}
	
	@Override
	public int compareTo(Element elem) {
		Element e = (Element) elem;
		if (this.fitness > e.fitness) return 1;
		if (this.fitness < e.fitness) return -1;
		return 0;
	}
	
	public synchronized void addFitness(double amt) {
		fitness += amt;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public int getGamesPlayed(){
		return gamesPlayed;
	}
	
	public int incrementGame(){
		return gamesPlayed++;
	}
}
