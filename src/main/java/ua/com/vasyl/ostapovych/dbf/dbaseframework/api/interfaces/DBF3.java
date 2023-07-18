package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;

import java.io.File;

public interface DBF3 {
    DBFReader<DBFRow>  getDBFReader(String dbfFileName);
    DBFReader<Object[]> getDBFRawReader(String dbfFileName);
    <T> DBFReader<T> getCustomDBFReader(String fileName, Class<T> type);
    DBFWriter  getDBFWriter(File filePath);
}
