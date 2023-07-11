package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions;

import java.util.NoSuchElementException;

public class DBFNoSuchRowException extends NoSuchElementException {

    public DBFNoSuchRowException(String s) {
        super(s);
    }
}
