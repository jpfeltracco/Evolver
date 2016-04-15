package core;

import ui.controllers.GUI;
import java.util.Random;

public class Main {
    public static Random rand = new Random();

    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        GUI.run();
    }
}
