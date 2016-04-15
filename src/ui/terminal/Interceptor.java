package ui.terminal;

import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Interceptor extends PrintStream {
    TextArea area;
    TextArea output;
    String consoleText = "";
    String outputText = "";

    public Interceptor(TextArea area, TextArea output) {
        super(System.out, true);
        this.area = area;
        this.output = output;
    }

    public String getConsoleText() {
        return consoleText;
    }

    public String getOutputText() {
        return outputText;
    }

    public synchronized void clearTerminal() {
        area.setText("");
        consoleText = "";
    }

    public void printToConsole(String s) {
        appendToOutput(s);
    }

    public void print(String tag, String s) {
        String t;
        if (tag.equals("System.in")) {
            t = "> " + s;
            appendToOutput(t);
        } else {
            t = "[" + tag + "] " + s;
            appendToConsole(t);
            super.print(t);
        }
    }

    public void print(String tag, boolean s) {
        String t;
        if (tag.equals("System.in")) {
            t = "> " + s;
            appendToOutput(t);
        } else {
            t = "[" + tag + "] " + s;
            appendToConsole(t);
            super.print(t);
        }
    }

    @Override
    public void print(String s) { //do what ever you like
        //String t = "[Console] " + s;
        appendToConsole(s);
        super.print(s);
        //super.print(s);
    }

    @Override
    public void print(Object t) { //do what ever you like
        //String t = "[Console] " + s;
        appendToConsole(t.toString());
        super.print(t);
        //super.print(s);
    }

    @Override
    public void print(boolean t) { //do what ever you like
        //String t = "[Console] " + b;
        appendToConsole("" + t);
        super.print(t);
    }

    public synchronized void appendToConsole(String s) {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        consoleText += "\n" + s;
                        area.appendText("\n" + s);
                    }
                });
    }

    public synchronized void appendToOutput(String s) {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        outputText += "\n" + s;
                        output.appendText("\n" + s);
                    }
                });
    }
}
