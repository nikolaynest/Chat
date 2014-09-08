package nikochat.com.app;

import nikochat.com.client.Client;
import nikochat.com.ui.TerminalUI;
import nikochat.com.ui.frames.RegisterFrame;

import javax.swing.*;

/**
 * Created by nikolay on 23.08.14.
 */
public class RunClient {


    public static void main(String[] args) {

//        new Client(new TerminalUI());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterFrame();
            }
        });
    }
}
