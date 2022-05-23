package exceptions;

public class UserAlreadyInProjectException extends Exception {
    public UserAlreadyInProjectException(String message) {
        super(message);
    }
}
