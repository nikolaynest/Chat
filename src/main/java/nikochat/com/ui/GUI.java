package nikochat.com.ui;

import javax.swing.*;

/**
 * Created by nikolay on 23.08.14.
 */
public class GUI implements UserInterface {
    private String ip;
    private String name;
    private int port;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getServerIP() {
        return ip;
    }

    @Override
    public String getClientName() {
        return name;
    }

    @Override
    public String write() {
        return null;
    }

    @Override
    public int getPort() {
        return port;
    }
}
