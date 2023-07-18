package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;

public class DBFReaderFactory {
    public static DBFReader<DBFRow> getDBFReader(String dbfFileName, DBFOptions options) {
        return new DBF3BaseReader(dbfFileName,options);
    }

    public static DBFReader<Object[]> getDBFRawReader(String dbfFileName, DBFOptions options) {
        return new DBFRawReader(dbfFileName,options);
    }

    public static <T> DBFReader<T> getCustomDBFReader(String dbfFileName,Class<T> type, DBFOptions options) {
        return new DBFAnnotatedReader<>(dbfFileName, options, type);
    }

}
