package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions;

public class DBFIllegalFieldException extends RuntimeException {
    public DBFIllegalFieldException() {
    }

    public DBFIllegalFieldException(String message) {
        super(message);
    }

    public DBFIllegalFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBFIllegalFieldException(Throwable cause) {
        super(cause);
    }
}
