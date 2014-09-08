package nikochat.com.exceptions;

import nikochat.com.app.AppConstants;

/**
 * Created by nikolay on 03.09.14.
 */
public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException() {
        super(AppConstants.REPEATED_NAME_MESSAGE);
    }
}
