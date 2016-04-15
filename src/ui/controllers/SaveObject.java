package ui.controllers;

import java.io.Serializable;

public class SaveObject implements Serializable {

    private static final long serialVersionUID = 8621299300296843879L;

    public byte[][] controller;
    public byte[][] simulation;
    public byte[][] elements;
    public byte[][][][] graph;
    public byte[][] evolve;
    public byte[][] goal;
    public byte[][] otherData;

    public SaveObject(int numElements) {
        controller = new byte[numElements][];
        simulation = new byte[numElements][];
        elements = new byte[numElements][];
        graph = new byte[numElements][][][];
        evolve = new byte[numElements][];
        goal = new byte[numElements][];
        otherData = new byte[numElements][];
    }

    public SaveObject() {
        this(1);
    }
}
