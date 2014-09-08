package nikochat.com.exceptions;

import nikochat.com.app.AppConstants;

/**
 * Created by nikolay on 06.09.14.
 */
public class ServerUnavailableException extends RuntimeException {
    public ServerUnavailableException() {
        super(AppConstants.SERVER_UNAVAILABLE_MESSAGE);
    }
}
