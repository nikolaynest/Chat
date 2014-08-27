import nikochat.com.app.AppConfig;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nikolay on 22.08.14.
 */
public class AppConfigTest {

    @Test
    public void configTestHost(){
        assertEquals("127.0.0.1", AppConfig.HOST);
    }
    @Test
    public void configTestPort(){
        assertEquals(8189, AppConfig.PORT);
    }


}
