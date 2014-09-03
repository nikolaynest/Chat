package nikochat.com.ui.frames;

import nikochat.com.app.AppConfig;
import nikochat.com.client.Client;
import nikochat.com.ui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nikolay on 02.09.14.
 */
public class Frame extends JFrame {

    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 250;
    private JLabel nameLabel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JButton confirm;
    private JButton cancel;
    private JTextField nameText;
    private JTextField ipText;
    private JComboBox<Integer> portChooser;
    private GUI gui;


    public Frame() throws HeadlessException {

        gui = new GUI();

        setTitle("Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(bag);

        gbc.weightx = 1.0;
        nameLabel = new JLabel("Имя пользователя:");
        nameText = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        bag.setConstraints(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 3, 3);
        bag.setConstraints(nameText, gbc);

        ipLabel = new JLabel("IP адрес:");
        ipText = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 1;
        bag.setConstraints(ipLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        bag.setConstraints(ipText, gbc);


        portLabel = new JLabel("Порт:");
        portChooser = new JComboBox<>(new Integer[]{AppConfig.PORT});
        gbc.gridx = 2;
        gbc.gridy = 1;
        bag.setConstraints(portLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        bag.setConstraints(portChooser, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(50, 0, 0, 0);
        confirm = new JButton("Вход");
        gbc.gridx = 0;
        gbc.gridy = 3;
        bag.setConstraints(confirm, gbc);


        gbc.gridx = 1;
        gbc.gridy = 3;
        cancel = new JButton("Отмена");
        bag.setConstraints(cancel, gbc);

        portChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setPort(Integer.valueOf((Integer) portChooser.getSelectedItem()));
            }
        });

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setName(nameText.getText());
                gui.setIp(ipText.getText());


                new EventQueue().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Client(gui);
                    }
                });
            }
        });

        add(nameLabel);
        add(nameText);
        add(ipLabel);
        add(ipText);
        add(portLabel);
        add(portChooser);
        add(confirm);
        add(cancel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frame();
            }
        });
    }
}
