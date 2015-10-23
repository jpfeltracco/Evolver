package com.jeremyfeltracco.core.evolver;

public class Element implements Comparable<Element> {
	public double config[];
	private double fitness = 0;
	
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
}
