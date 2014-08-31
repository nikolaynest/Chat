package nikochat.com.app;

import nikochat.com.client.Client;
import nikochat.com.exceptions.MaxUsersException;
import nikochat.com.ui.TerminalUI;

/**
 * Created by nikolay on 23.08.14.
 */
public class RunClient {
    public static void main(String[] args) {
        new Client(new TerminalUI());
    }
}
