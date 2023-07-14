package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFieldNotFoundException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;

import java.util.*;

class DBFRawReaderImpl implements DBFReader<Object[]> {
    private final DBFReader<DBFRow> baseReader;

    DBFRawReaderImpl(String dbfFileName, DBFOptions options) {
        baseReader =  new DBF3BaseReader(dbfFileName,options);
    }

    @Override
    public long getRowCount() {
        return baseReader.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return baseReader.getColumnCount();
    }

    @Override
    public List<Object[]> readAllRows() {
        return toRawArray(baseReader.readAllRows());
    }

    private List<Object[]> toRawArray(List<DBFRow> readAllRows) {
        if (readAllRows == null) return Collections.emptyList();
        List<Object[]> res = new ArrayList<>();
        for (DBFRow row:readAllRows){
            res.add(row.toRawRow());
        }
        return res;
    }

    @Override
    public List<Object[]> readByFilter(DBFFilter filter) {
        return toRawArray(baseReader.readByFilter(filter));
    }


    @Override
    public List<Object[]> readByFilter(Set<DBFFilter> filters) {
        return null;
    }

    @Override
    public DBFField[] getFields() {
        return baseReader.getFields();
    }

    @Override
    public Object[] readCurrentRow() {
        return baseReader.readCurrentRow().toRawRow();
    }

    @Override
    public  List<Object[]> readRange(long start, long end) {
        return toRawArray(baseReader.readRange(start,end));
    }

    @Override
    public void close() {
        this.baseReader.close();
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            private final Iterator<DBFRow> iterator = baseReader.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Object[] next() {
                return iterator.next().toRawRow();
            }

            @Override
            public void remove() {
                throw new DBFFieldNotFoundException("Not supported");
            }
        };
    }
}
