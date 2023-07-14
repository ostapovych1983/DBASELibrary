package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBF3;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;

import java.io.File;

public class DBF3Impl implements DBF3 {
    private final DBFOptions options;

    public DBF3Impl(DBFOptions options) {
        this.options = options;
    }

    @Override
    public DBFReader<DBFRow> getDBFReader(String dbfFileName) {
        return new DBF3BaseReader(dbfFileName,options);
    }

    @Override
    public DBFReader<Object[]> getDBFRawReader(String dbfFileName) {
        return new DBFRawReaderImpl(dbfFileName,options);
    }

    @Override
    public <T> DBFReader<T> getCustomDBFReader(String dbfFileName,Class<T> type) {
        return new DBFAnnotatedReader<>(dbfFileName, options, type);
    }

    @Override
    public DBFWriter getDBFWriter(File filePath) {
        return new DBFBaseWriter(filePath);
    }

}
