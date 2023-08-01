package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions;

public class DBFFileOpenException extends RuntimeException{

    public DBFFileOpenException(String message) {
        super(message);
    }

    public DBFFileOpenException(Throwable cause) {
        super(cause);
    }

}
