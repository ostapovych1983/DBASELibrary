package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions;

public class DBFFieldNotFoundException extends RuntimeException {
    public DBFFieldNotFoundException() {
    }

    public DBFFieldNotFoundException(String message) {
        super(message);
    }

    public DBFFieldNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBFFieldNotFoundException(Throwable cause) {
        super(cause);
    }
}
