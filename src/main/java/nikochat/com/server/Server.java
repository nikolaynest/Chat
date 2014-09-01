package nikochat.com.server;

import nikochat.com.app.AppConfig;
import nikochat.com.app.AppConstants;
import nikochat.com.service.Log;
import nikochat.com.service.StreamsManager;
import nikochat.com.ui.ServerMenu;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by nikolay on 23.08.14.
 */
public class Server {


    private ServerSocket server;
    private final Map<String, ServerThread> clients = Collections.synchronizedMap(new TreeMap<>());
    private final Queue<String> history = new ConcurrentLinkedQueue<String>();

    public Server() {
        System.out.println("Server is running...");
        Log.write("Server is running...");

        new Thread(new ServerMenu(this)).start();

        try {
            server = new ServerSocket(AppConfig.PORT);
        } catch (IOException e) {
            System.out.println("Error creating server");
            Log.write("Error creating server");
            Log.write(e.getMessage());
            e.printStackTrace();
        }


        while (true) {
            try {
                Socket accept = server.accept();
                Log.write("server accept socket");
                ServerThread serverThread = new ServerThread(accept);
                new Thread(serverThread).start();
                Log.write("server start new ServerThread");
            } catch (IOException e) {
                System.out.println("Error accepting client on server");
                Log.write("Error accepting client on server");
                Log.write(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private class ServerThread implements Runnable {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;

        public ServerThread(Socket socket) {
            this.socket = socket;
            in = StreamsManager.createInput(socket, this.getClass());
            out = StreamsManager.createOutput(socket, this.getClass());
        }

        @Override
        public void run() {
            try {
                boolean goFurther = true; /*аварийный выход*/
                /** первым делом получаю имя нового "клиента" */
                try {
                    goFurther = readClientName();
                } catch (IOException e) {
                    System.out.println("Error reading name from client...");
                    Log.write("Error reading name from client...");
                    Log.write(e.getMessage());
                    e.printStackTrace();
                }
                if (goFurther) {
                    String time = getTimeWithoutMillis(LocalTime.now());
                    String invitation = time + " " + name + " has joined";
                    printHistory();
                    addToHistory(invitation);

                    System.out.println(time + "  " + name + " has joined");
                    System.out.println("numbers of users: " + clients.size());
                    resendMessage(invitation);

                    /** читаю из входящего потока сообщения */
                    while (true) {
                        String received = null;
                        try {
                            received = in.readLine();
                            time = getTimeWithoutMillis(LocalTime.now());
                        } catch (IOException e) {
                            System.out.println("Error reading message from client...");
                            Log.write("Error reading message from client...");
                            Log.write(e.getMessage());
                            e.printStackTrace();
                        }
                        if (received == null) {
                            Log.write("received message from client is null");
                            break;
                        }

                        if (!received.trim().equals("exit")) {
                            String local = time + " " + name + ": " + received;
                            resendMessage(local);
                            addToHistory(local);
                        } else {
                            received = time + " " + name + " exit from chat";
                            addToHistory(received);
                            resendMessage(received);
                            out.println("exit");
                            System.out.println(received);
                            Log.write(received);
                            break;
                        }
                    }
                }
            } finally {
                try {
                    closeConnection();
                    clients.remove(name);
                } catch (IOException e) {
                    System.out.println("Error closing socket on server side");
                    Log.write("Error closing socket on server side");
                    Log.write(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        private void printHistory() {
            synchronized (history) {
                for (String s : history) {
                    out.println(s);
                }
            }
        }

        private boolean readClientName() throws IOException {
            boolean continueProgram = true;
            while (true) {
                name = in.readLine();
                if (name == null) {
                    continueProgram = false;
                    Log.write("read name is null");
                    break;
                }
                if (!(clients.size() < AppConfig.MAX_USERS)) {
                    out.println("MAX");
                    continueProgram = false;
                    Log.write("reduce register new connection");
                    break;
                }
                if (clients.get(name) == null) {
                    clients.put(name, this);
                    Log.write("register new user with the name: " + name);
                    break;
                } else {
                    out.println(AppConstants.REPEATED_NAME_MESSAGE);
                    out.print("> ");
                }
            }
            return continueProgram;
        }

        private void resendMessage(String message) {
            synchronized (clients) {
                for (ServerThread st : clients.values()) {
                    st.out.println(message);
                }
            }
        }

        private String getTimeWithoutMillis(LocalTime time) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US);
            return formatter.format(time);
        }

        private void closeConnection() throws IOException {
            in.close();
            out.close();
//            StreamsManager.closeInput(in, this.getClass());
//            StreamsManager.closeOutput(out);
            socket.close();
            Log.write("close 'input', 'output' and 'socket' for user with the name: "+name);
        }
    }

    private void addToHistory(String mess) {
        if (history.size() >= AppConfig.NUM_HISTORY_MESSAGES) {
            history.remove();
        }
        history.add(mess);
    }

    public void killSocket(String name) {
        ServerThread st = clients.get(name);
        if (st == null) {
            System.out.println("Нет пользователя с таким именем");
        } else {
            st.out.println("denied");
            try {
                st.closeConnection();
                Log.write("kill socket with the name " + name);
            } catch (IOException e) {
                System.out.println("Error close connection killing user " + name);
                Log.write("Error close connection killing user " + name);
                Log.write(e.getMessage());
                e.printStackTrace();
            }
            clients.remove(name);
            synchronized (clients) {
                for (ServerThread thread : clients.values()) {
                    thread.out.println("Пользователь " + name + " отсоединен.");
                }
            }

        }
    }

    public void list() {
        System.out.println("Список всех подключенных клиентов:");
        if (clients.size() == 0) {
            System.out.println("0 клиентов");
        } else {
            synchronized (clients) {
                clients.keySet().forEach(System.out::println);
            }
        }
    }
}
