package nikochat.com.exceptions;

/**
 * Created by nikolay on 30.08.14.
 */
public class MaxUsersException extends Exception {
    public MaxUsersException() {
        super("Достигнуто максимальное количество пользователей.");
    }
}
