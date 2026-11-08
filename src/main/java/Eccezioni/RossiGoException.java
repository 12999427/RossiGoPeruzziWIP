package Eccezioni;

public class RossiGoException extends Exception {
    public RossiGoException() {
        super();
    }

    public RossiGoException(String message) {
        super(message);
    }

    public RossiGoException(String message, Throwable cause) {
        super(message, cause);
    }
}

