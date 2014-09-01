package nikochat.com.exceptions;

import java.io.IOException;

/**
 * Created by nikolay on 01.09.14.
 */
public class KillSocketException extends IOException{
    public KillSocketException(String message) {
        super(message);
    }
}
