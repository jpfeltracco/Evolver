package ui.terminal;

import java.io.IOException;
import java.io.InputStream;

import ui.controllers.FXController;

public class TerminalScanner {
    InputStream inputStream;

    public TerminalScanner(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String nextLine() {
        try {
            String out = "";
            int in = inputStream.read();
            //FXController.out.print("IN: " + in);
            if (in == -1) {

                return null;
            }
            while (in != 0 && in != -1) {
                //FXController.out.print("TEST","Value: " + in);
                out += (char) in;
                in = inputStream.read();
            }
            //FXController.out.print("TEST","Output: " + out + "\n");
            return out;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
