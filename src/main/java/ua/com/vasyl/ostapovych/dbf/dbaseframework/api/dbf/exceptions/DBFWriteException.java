package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions;

public class DBFWriteException extends RuntimeException {
    public DBFWriteException(String message) {
        super(message);
    }

    public DBFWriteException(Throwable e) {
        super(e);
    }
}
