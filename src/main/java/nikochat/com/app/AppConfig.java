package nikochat.com.app;

import java.io.*;
import java.util.Properties;

/**
 * Created by nikolay on 22.08.14.
 */
public final class AppConfig {

    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String separator = System.getProperty("file.separator");
    public static final String RELATIVE_LOG_PATH = System.getProperty("user.home")+separator+"err_action.log";

    static {
        File logfile = new File(RELATIVE_LOG_PATH);
        if (!logfile.exists()){
            try {
                logfile.createNewFile();
            } catch (IOException e) {
                System.out.println("can't create file "+RELATIVE_LOG_PATH);
                e.printStackTrace();
            }
        }
    }

//    private static InputStream logInputStream = this.getClass().getClassLoader().getResourceAsStream(RELATIVE_LOG_PATH);
//    private static InputStream propsInputStream = this.getClass().getClassLoader().getResourceAsStream(CONFIG_PROPERTIES);

    private static Properties props = new AppConfig().initProperties();
    public static final String HOST = props.getProperty("ip_address");

    public static final int PORT = Integer.valueOf(props.getProperty("port"));
    public static final int MAX_USERS = Integer.valueOf(props.getProperty("max_number_of_users"));
    public static final int NUM_HISTORY_MESSAGES = Integer.valueOf(props.getProperty("last_N_messages"));

    private Properties initProperties() {
        Properties properties = null;
//        try (FileInputStream input = new FileInputStream(CONFIG_PROPERTIES)) {
            properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(CONFIG_PROPERTIES));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return properties;
    }

}
