package nikochat.com.server;

import nikochat.com.app.AppConfig;
import nikochat.com.app.AppConstants;
import nikochat.com.service.StreamsManager;
import nikochat.com.ui.ServerMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by nikolay on 23.08.14.
 */
public class Server {


    private ServerSocket server;
    private final Map<String, ServerThread> clients = Collections.synchronizedMap(new TreeMap<>());

    public Server() {
        System.out.println("Server is running...");
        new Thread(new ServerMenu(this)).start();

        try {
            server = new ServerSocket(AppConfig.PORT);
        } catch (IOException e) {
            System.out.println("Error creating server");
            e.printStackTrace();
        }


        while (true) {
            try {
                Socket accept = server.accept();
                ServerThread serverThread = new ServerThread(accept);
                new Thread(serverThread).start();
                System.out.println("create user");
            } catch (IOException e) {
                System.out.println("Error accepting client on server");
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
                    e.printStackTrace();
                }
                if (goFurther) {
                    String time = getTimeWithoutMillis(LocalTime.now());
                    System.out.println(time + "  " + name + " has joined");
                    System.out.println("numbers of users: " + clients.size());
                    sendMessage(time, " has joined");

                    /** читаю из входящего потока сообщения */
                    while (true) {
                        String message = null;
                        try {
                            message = in.readLine();
                        } catch (IOException e) {
                            System.out.println("Error reading message from client...");
                            e.printStackTrace();
                        }
                        if (message == null) {
                            break;
                        }
                        time = getTimeWithoutMillis(LocalTime.now());
                        if (sendMessage(time, message)) {
                            System.out.println(time + "  " + message);
                        } else {
                            sendMessage(time, message);
                            ServerThread exitClient = clients.get(name);
                            exitClient.out.println(AppConstants.EXIT);
                            System.out.println(time + "  " + name + " exit from chat");
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
                    e.printStackTrace();
                }
            }
        }

        private boolean readClientName() throws IOException {
            boolean continueProgram = true;
            while (true) {
                name = in.readLine();
                if (name == null) {
                    continueProgram = false;
                    break;
                }
                if (!(clients.size() < AppConfig.MAX_USERS)) {
                    out.println("MAX");
                    continueProgram = false;
                    break;
                }
                if (clients.get(name) == null) {
                    clients.put(name, this);
                    break;
                } else {
                    out.println(AppConstants.REPEATED_NAME_MESSAGE);
                    out.print("> ");
                }
            }
            return continueProgram;
        }

        private boolean sendMessage(String time, String message) {
            boolean continueProgram = true;
            if (message.trim().equals("exit")) {
                message = " exit from chat";
                ;
                continueProgram = false;
            }
            synchronized (clients) {
                for (ServerThread st : clients.values()) {
                    st.out.println(time + "  " + name + ": " + message);
                }
            }
            return continueProgram;
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
        }
    }

    public void killSocket(String name) {
        ServerThread st = clients.get(name);
        if (st == null) {
            System.out.println("Нет пользователя с таким именем");
        } else {
            st.out.println("denied");
            try {
                st.closeConnection();
            } catch (IOException e) {
                System.out.println("Error close connection killing user " + name);
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
