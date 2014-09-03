package nikochat.com.client;

import nikochat.com.app.AppConfig;
import nikochat.com.app.AppConstants;
import nikochat.com.service.StreamsManager;
import nikochat.com.ui.UserInterface;

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
    private BufferedReader input;
    private PrintWriter output;
    private boolean stopped;
    private String name;

    public Client(UserInterface ui) {

        synchronized (this) {
            socket = connectToServer(ui.getServerIP(), ui.getPort());
            input = StreamsManager.createInput(socket, this.getClass());
            output = StreamsManager.createOutput(socket, this.getClass());
            name = ui.getClientName();
            register(name);
        }

        /**получение сообщений*/
        new Thread(new ReceiveMessage()).start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /** Отправление сообщений*/
        try {
            while (!stopped) {
                String message = ui.write();
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

    private class ReceiveMessage implements Runnable {
        @Override
        public void run() {
            while (!stopped) {
                try {
                    String receive = input.readLine();
                    if (receive != null) {
                        switch (receive) {
                            case "MAX":
                                System.out.println("Достигнуто максимальное количество пользователей");
                                stopped = true;
                                break;
                            case "exit":
                                break;
                            case "denied":
                                System.out.println("Сервер недоступен");
                                stopped = true;
                                break;
                            default:
                                System.out.println(receive);
                        }
                    } else {
                        System.out.println(AppConstants.SERVER_UNAVAILABLE_MESSAGE);
                        close();
                        break;
                    }
                } catch (IOException e) {
                    stopped = true;
                    System.out.println("Error receiving message from server ");
                    e.printStackTrace();
                }
            }
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

    private void register(String name) {
        output.println(name);
    }

    private synchronized void close() throws IOException {
        StreamsManager.closeInput(input, this.getClass());
        StreamsManager.closeOutput(output);
        socket.close();
    }
}
