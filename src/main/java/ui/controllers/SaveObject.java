package ui.controllers;

import java.io.Serializable;

import ui.Builder.InputFramework;

public class SaveObject implements Serializable{
	private static final long serialVersionUID = 8621299300296843879L;
	
	public byte[] controller;
	public byte[] simulation;
	public byte[] elements;
	public byte[][] graph;
	public byte[] evolve;
}
