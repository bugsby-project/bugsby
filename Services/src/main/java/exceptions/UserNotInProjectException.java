package exceptions;

public class UserNotInProjectException extends Exception {
    public UserNotInProjectException(String message) {
        super(message);
    }
}
