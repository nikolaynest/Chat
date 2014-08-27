package nikochat.com.server;

import nikochat.com.app.AppConfig;
import nikochat.com.service.StreamsManager;
import nikochat.com.ui.ServerMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by nikolay on 23.08.14.
 */
public class Server /*implements Runnable*/ {

    private ServerSocket server;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    //TODO: WHAT BETTER TO USE: LIST OR MAP, OR HOW TO SAVE USER INFO?
    private Map<String, ServerThread> map = Collections.synchronizedMap(new HashMap<>());
    private List<ServerThread> threads = Collections.synchronizedList(new ArrayList<>());


    public Server() {
        System.out.println("Server is running...");
        try {
            server = new ServerSocket(AppConfig.PORT);
        } catch (IOException e) {
            System.out.println("Error creating server");
            e.printStackTrace();
        }

        /** connect socket to server */
//        new Thread(this).start();
        new Thread(new ServerMenu(this)).start();

        while (true) {
            try {
                socket = server.accept();
//                input = StreamsManager.createInput(socket, this.getClass());
//                output = StreamsManager.createOutput(socket, this.getClass());


                ServerThread serverThread = new ServerThread(socket);
                threads.add(serverThread);

                new Thread(serverThread).start();

            } catch (IOException e) {
                System.out.println("Error accepting client on server");
                e.printStackTrace();
            }

        }

    }

//    @Override
//    public void run() {
//        while (true) {
//            try {
//                socket = server.accept();
////                input = StreamsManager.createInput(socket, this.getClass());
////                output = StreamsManager.createOutput(socket, this.getClass());
//            } catch (IOException e) {
//                System.out.println("Error accepting client on server");
//                e.printStackTrace();
//            }
//
//            ServerThread serverThread = new ServerThread(socket);
//            threads.add(serverThread);
//            Thread t = new Thread(serverThread);
//            t.start();
//        }
//    }

    public void killSocket(String name) {
        //TODO:
        System.out.println("kill " + name);
    }

    private class ServerThread implements Runnable {

        private BufferedReader in;
        private PrintWriter out;

        //todo: возможно, передавать in и out параметрами конструктора
        public ServerThread(Socket socket) {

            in = StreamsManager.createInput(socket, this.getClass());
            out = StreamsManager.createOutput(socket, this.getClass());
        }

        @Override
        public void run() {
            try {
                /** первым делом получаю имя нового "клиента" */
                String name = in.readLine();
                System.out.println(name + " has joined");
                System.out.println("numbers of users: " + threads.size());

                synchronized (threads) {
                    for (ServerThread st : threads) {
                        st.out.println(name + " has joined");
                    }
                }

                /** читаю из входящего потока сообщения */
                String message = "";
                while (true) {
                    message = in.readLine();
                    if (!message.trim().equals("exit")) {
                        synchronized (threads) {
                            for (ServerThread st : threads) {
                                st.out.println(message);

                            }
                        }
                        System.out.println(message);
                    } else {
                        synchronized (threads) {
                            for (ServerThread st : threads) {
                                st.out.println(name + " exit from chat");
                            }
                        }
                        System.out.println(name + " exit from chat");
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
