package nikochat.com.ui.frames;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by nikolay on 02.09.14.
 */
public class MainFrame extends JFrame {

    public MainFrame(String name, java.util.List<String> users) throws HeadlessException {
        super();
        setTitle("Чат - "+name);
        Box list = Box.createVerticalBox();
        list.setBorder(new TitledBorder("Пользователи"));

    }
}
