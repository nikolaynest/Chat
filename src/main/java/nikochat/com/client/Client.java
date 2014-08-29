package nikochat.com.client;

import nikochat.com.app.AppConfig;
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
            socket = connectToServer(ui.getServerIP());
            input = StreamsManager.createInput(socket, this.getClass());
            output = StreamsManager.createOutput(socket, this.getClass());
            register(ui.getClientName());
        }

        /**получение сообщений*/
        new Thread(new ReceiveMessage()).start();

        try {
            while (true) {
                String message = ui.write();
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
        }
    }

    private Socket connectToServer(String ip) {
        Socket socket = null;
        try {
            socket = new Socket(ip, AppConfig.PORT);
        } catch (IOException e) {
            System.out.println("Error creating socket in client");
            e.printStackTrace();
        }
        return socket;
    }

    private void register(String name) {
        this.name = name;
        output.println(name);
    }

    private synchronized void close() throws IOException {
        StreamsManager.closeInput(input, this.getClass());
        StreamsManager.closeOutput(output);
        socket.close();
    }

    private class ReceiveMessage implements Runnable{
        @Override
        public void run() {
            while (!stopped) {
                try {
                    String receive = input.readLine();
                    System.out.println(receive);
                } catch (IOException e) {
                    stopped = true;
                    System.out.println("Error receiving message from server ");
                    e.printStackTrace();
                }
            }
        }
    }
}
