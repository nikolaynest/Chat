package nikochat.com.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nikolay on 22.08.14.
 */
public final class AppConfig {

    private static final String RELATIVE_PATH_TO_PROPERTIES = "./src/main/resources/config.properties";
    private static Properties props = initProperties();

    public static final String HOST = props.getProperty("host");
    public static final int PORT = Integer.valueOf(props.getProperty("port"));

    private static Properties initProperties() {
        Properties properties = null;
        try (FileInputStream input = new FileInputStream(RELATIVE_PATH_TO_PROPERTIES)) {
            properties = new Properties();
            properties.load(input);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


}
