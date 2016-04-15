package evolver;

import java.io.Serializable;

public class Element implements Comparable<Element>, Serializable {
    public double config[];
    private double fitness = 0;
    private int gamesPlayed = 0;
    public static int numElements = 0;
    public int id;

    public Element() {
        id = numElements;
        numElements++;
    }

    @Override
    public synchronized int compareTo(Element elem) {
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

    public void setFitness(double val) {
        fitness = val;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int incrementGame() {
        gamesPlayed++;
        return gamesPlayed;
    }

    public Element clone() {
        Element e = new Element();
        e.setFitness(fitness);
        e.config = this.config.clone();
        return e;
    }
}
