package nikochat.com.app;

import nikochat.com.client.Client;
import nikochat.com.ui.TerminalUI;

/**
 * Created by nikolay on 23.08.14.
 */
public class RunClient {
    public static void main(String[] args) {
        Client client = new Client(new TerminalUI());
    }
}
