package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;

import java.io.File;

public class DBFWriterFactory {

    public static DBFWriter getDBFWriter(File filePath) {
        return new DBFBaseWriter(filePath);
    }
}
