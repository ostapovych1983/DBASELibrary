package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.validation;

public class DBFValidationException extends RuntimeException{
    public DBFValidationException() {
    }

    public DBFValidationException(String message) {
        super(message);
    }

    public DBFValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBFValidationException(Throwable cause) {
        super(cause);
    }

    public DBFValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
