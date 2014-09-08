package nikochat.com.client;

import nikochat.com.app.AppConstants;
import nikochat.com.exceptions.DuplicateNameException;
import nikochat.com.exceptions.MaxUsersException;
import nikochat.com.exceptions.ServerUnavailableException;
import nikochat.com.service.StreamsManager;
import nikochat.com.ui.TerminalUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Created by nikolay on 23.08.14.
 */
public class Client {


    private Socket socket;

    private PrintWriter output;
    private BufferedReader input;
    private boolean stopped;
    private String name;
    private TerminalUI ui;


    public Client() {
//        connect(AppConfig.HOST, AppConfig.PORT);
//        register(new TerminalUI().getClientName());
//        new Thread(new ReceiveMessage()).start();
//        send();
    }

    public String getName() {
        return name;
    }


    /**
     * This method must firstly be invoked to make a connection with the server
     * initialise input and output streams,
     * and addObserver the user in the chat.
     *
     * @param ip
     * @param port
     */
    public synchronized void connect(String ip, int port) {
        socket = connectToServer(ip, port);
        output = StreamsManager.createOutput(socket, this.getClass());
        input = StreamsManager.createInput(socket, this.getClass());
    }

    public synchronized void close() throws IOException {
        StreamsManager.closeInput(input, this.getClass());
        StreamsManager.closeOutput(output);
        socket.close();
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }


    /**
     * Must be invoked after connect() method
     *
     * @param name is the username for registering in the chat
     */
    public void register(String name) {
        output.println(name);
        String receive = null;
        try {
            receive = input.readLine();
            if (receive != null) {
                switch (receive) {
                    case AppConstants.REPEATED_NAME_ERROR:
                        System.out.println(AppConstants.REPEATED_NAME_MESSAGE);
                        throw new DuplicateNameException();
                    case AppConstants.OK_REGISTERED:
                        this.name = name;
                        break;
                    case AppConstants.MAX_USERS_ERROR:
                        System.out.println("Достигнуто максимальное количество пользователей");
                        stopped = true;
                        throw new MaxUsersException();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * receiving messages. For running in terminal
     */
    public void receive() {
        new Thread(new ReceiveMessage()).start();
    }


    /**
     * send messages. For running in terminal
     */
    public void send() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /** Sending messages */
        try {
            while (!stopped) {
                String message = ui.read();
                if (stopped) break;
                if (message.equals("")) continue;
                output.println(message);
                if (message.trim().equals("exit")) {
                    stopped = true;
                    break;
                }
            }
            close();
        } catch (IOException e) {
            System.out.println("Error closing socket");
            e.printStackTrace();
            /** аварийный выход */
        } catch (NoSuchElementException n) {
            stopped = true;
            output.println("exit");
        }
    }

    private Socket connectToServer(String ip, int port) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            System.out.println("Error creating socket in client");
            e.printStackTrace();
        }
        return socket;
    }

    class ReceiveMessage implements Runnable {
        @Override
        public void run() {
            while (!stopped) {
                try {
                    String receive = input.readLine();
                    if (receive != null) {
                        switch (receive) {
                            case "exit":
                                break;
                            case "denied":
                                stopped = true;
                                throw new ServerUnavailableException();
                            case AppConstants.LIST:
                                System.out.println("LIST:");
                                System.out.println(input.readLine());
                                break;
                            default:
                                ui.write(receive);
                        }
                    } else {
                        close();
                        throw new ServerUnavailableException();
                    }
                } catch (IOException e) {
                    stopped = true;
                    System.out.println("Error receiving message from server ");
                    e.printStackTrace();
                }
            }
        }

    }
}
