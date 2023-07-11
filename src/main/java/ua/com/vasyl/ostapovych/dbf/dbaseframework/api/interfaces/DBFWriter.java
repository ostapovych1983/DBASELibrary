package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.util.List;

public interface DBFWriter {
    <T> void writeRows(List<T> rows,Class<T> tClass);
    void writeRows(DBFField [] fields,Object [][] rows);
}
