package nikochat.com.ui;

import nikochat.com.app.AppConfig;

import java.util.Scanner;

/**
 * Created by nikolay on 23.08.14.
 */
public class TerminalUI implements UserInterface {

    private Scanner scanner;

    public TerminalUI() {
        scanner = new Scanner(System.in);
    }

    @Override
    public String getServerIP() {
        System.out.println("Enter IP for connection to server");
        System.out.println("Format: XXX.XXX.XXX.XXX");
        System.out.print("_______>");
        return scanner.nextLine();
    }

    @Override
    public String getClientName() {
        System.out.print("Enter your name: >");
        return scanner.nextLine();
    }

    @Override
    public String write() {
        return scanner.nextLine();
    }

    @Override
    public int getPort() {
        return AppConfig.PORT;
    }


}
