package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;

import java.io.File;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions.defaultOptions;

public class DBFWriterFactory {
    public static DBFWriter getDBFWriter(File filePath) {
        return new DBFBaseWriter(filePath, defaultOptions());
    }

    public static DBFWriter getDBFWriter(File filePath,DBFOptions dbfOptions) {
        return new DBFBaseWriter(filePath, dbfOptions);
    }
}
