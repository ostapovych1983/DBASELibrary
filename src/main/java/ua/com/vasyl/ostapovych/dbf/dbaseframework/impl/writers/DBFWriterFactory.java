package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLogger;

import java.io.File;

public class DBFWriterFactory {

    public static DBFWriter getDBFWriter(File filePath, DBFLogger dbfLogger) {
        return new DBFBaseWriter(filePath, dbfLogger);
    }
}
