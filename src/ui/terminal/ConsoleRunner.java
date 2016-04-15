package ui.terminal;

import ui.controllers.FXController;
import ui.controllers.GUI;

public class ConsoleRunner implements Runnable {

    TerminalScanner scan = new TerminalScanner(System.in);
    String command;

    @Override
    public void run() {
        FXController.out.printToConsole("> Evolver Console");
        while (GUI.running) {
            command = scan.nextLine();

            if (command == null) continue;

            switch (command.trim().toLowerCase()) {
                case "help":
                case "?":
                case "-h":
                    FXController.out.printToConsole("System Help");
                    FXController.out.printToConsole("-blah");
                    FXController.out.printToConsole("-stuff");
                    FXController.out.printToConsole("-yay!");
                    break;
                case "exit":
                    FXController.out.printToConsole("Closing...");
                    GUI.running = false;
                    break;
            }
        }
    }
}
