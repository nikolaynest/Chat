package nikochat.com.ui.frames;

import nikochat.com.app.AppConstants;
import nikochat.com.client.Client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by nikolay on 02.09.14.
 */
public class MainFrame extends JFrame implements ActionListener {

    private static final int MY_WIDTH = 800;
    private static final int MY_HEIGHT = 500;
    private JButton button;
    private JTextArea writeMessageArea;
    private JTextArea readMessageArea;
    private JTextArea listArea;

    private final int topGap = 10;
    private final int leftGap = 10;
    private final int bottomGap = 10;
    private final int rightGap = 10;

    private final Client client;

    public MainFrame(Client client) throws HeadlessException {

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(MY_WIDTH, MY_HEIGHT);
        setMinimumSize(new Dimension(400, 300));
        setTitle("Чат - " + client.getName());
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(topGap, leftGap, bottomGap, rightGap));
        JSplitPane messagesSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, messagesPanel(), writeMessagePanel());
        JSplitPane listSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, listPanel(), messagesSplit);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.getOutput().println(AppConstants.EXIT);
                    client.close();
                    System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String line = client.getInput().readLine();

                        if (line != null) {
                            switch (line) {
                                case AppConstants.LIST:
                                    listArea.setText("");
                                    String str;
                                    while (!(str = client.getInput().readLine()).equals(AppConstants.LIST)) {
                                        listArea.append(str);
                                        listArea.append("\n");
                                    }
                                    break;
                                case AppConstants.DENIED:
                                    client.close();
                                    JOptionPane.showMessageDialog(null,
                                            AppConstants.SERVER_UNAVAILABLE_MESSAGE, "Info", JOptionPane.INFORMATION_MESSAGE);
                                    System.exit(0);
                                default:
                                    readMessageArea.append(line + "\n");
                            }
                        } else {
                            client.close();
                            JOptionPane.showMessageDialog(null,
                                    AppConstants.SERVER_UNAVAILABLE_MESSAGE, "Info", JOptionPane.INFORMATION_MESSAGE);
                            System.exit(0);
                        }
                    }
                } catch (IOException ignored) {
                    /*throws when user close the window*/
                }
            }
        }).start();

        mainPanel.add(listSplit, BorderLayout.CENTER);
        mainPanel.add(buttonPanel(), BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = writeMessageArea.getText();
        System.out.println(message);
        if (!message.equals("")) {
            client.getOutput().println(message);
        }
        writeMessageArea.setText("");
    }

    private JPanel listPanel() {
        int localHeight = (int) (MY_HEIGHT - MY_HEIGHT * 0.2);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setSize(new Dimension(MY_WIDTH / 3, localHeight));
        panel.setMinimumSize(new Dimension(MY_WIDTH / 4, localHeight));
        setLayout(new BorderLayout());

        listArea = new JTextArea();
        listArea.setEditable(false);
        listArea.setLineWrap(true);
        panel.add(new JScrollPane(listArea), BorderLayout.CENTER);
        panel.setBorder(new TitledBorder("Пользователи"));
        return panel;
    }

    private JPanel messagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Окно сообщений"));
        panel.setMinimumSize(new Dimension(MY_WIDTH / 3, MY_HEIGHT / 2));
        panel.setPreferredSize(new Dimension(2 / 3 * MY_WIDTH, MY_HEIGHT / 2));
        readMessageArea = new JTextArea();
        readMessageArea.setLineWrap(true);
        readMessageArea.setEditable(false);
        panel.add(new JScrollPane(readMessageArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel writeMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Введите текст сообщения"));
        writeMessageArea = new JTextArea();
        writeMessageArea.setLineWrap(true);
        writeMessageArea.setEditable(true);
        panel.add(writeMessageArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buttonPanel() {
        button = new JButton("Отправить");
        button.addActionListener(this);
        JPanel butPanel = new JPanel(new BorderLayout());
        butPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        butPanel.setPreferredSize(new Dimension(WIDTH, 30));
        butPanel.add(button, BorderLayout.EAST);
        return butPanel;
    }
}
