package nikochat.com.server;

import nikochat.com.app.AppConfig;
import nikochat.com.service.StreamsManager;
import nikochat.com.ui.ServerMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by nikolay on 23.08.14.
 */
public class Server {

    private ServerSocket server;
    //TODO: WHAT BETTER TO USE: LIST OR MAP, OR HOW TO SAVE USER INFO?
    private final Map<String, ServerThread> map = Collections.synchronizedMap(new HashMap<>());
    private final List<ServerThread> threads = Collections.synchronizedList(new ArrayList<>());


    public Server() {
        System.out.println("Server is running...");
        try {
            server = new ServerSocket(AppConfig.PORT);
        } catch (IOException e) {
            System.out.println("Error creating server");
            e.printStackTrace();
        }

        new Thread(new ServerMenu(this)).start();

        while (true) {
            try {
                ServerThread serverThread = new ServerThread(server.accept());
                threads.add(serverThread);
                new Thread(serverThread).start();
            } catch (IOException e) {
                System.out.println("Error accepting client on server");
                e.printStackTrace();
            }
        }
    }

    public void killSocket(String name) {
        //TODO:
        System.out.println("kill " + name);
    }

    private class ServerThread implements Runnable {

        private BufferedReader in;
        private PrintWriter out;

        public ServerThread(Socket socket) {
            in = StreamsManager.createInput(socket, this.getClass());
            out = StreamsManager.createOutput(socket, this.getClass());
        }

        private String getTime(){
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return formatter.format(time);
        }
        @Override
        public void run() {

            try {
                /** первым делом получаю имя нового "клиента" */
                String name = in.readLine();
                String time = getTime();
                System.out.println(time + "  " + name + " has joined");
                System.out.println("numbers of users: " + threads.size());
                synchronized (threads) {
                    for (ServerThread st : threads) {
                        st.out.println(time + "  " + name + " has joined");
                    }
                }
                /** читаю из входящего потока сообщения */
                while (true) {
                    String message = in.readLine();
                    time = getTime();
                    if (!message.trim().equals("exit")) {
                        synchronized (threads) {
                            for (ServerThread st : threads) {
                                st.out.println(time + "  " + message);

                            }
                        }
                        System.out.println(time + "  " + message);
                    } else {
                        synchronized (threads) {
                            for (ServerThread st : threads) {
                                st.out.println(time + "  " + name + " exit from chat");
                            }
                        }
                        System.out.println(time + "  " + name + " exit from chat");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading name from client...");
                e.printStackTrace();
            }
        }
    }
}
