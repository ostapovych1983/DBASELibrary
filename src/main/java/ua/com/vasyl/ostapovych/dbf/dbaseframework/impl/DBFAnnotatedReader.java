package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.validators.Validators.validateMetaDataDBF;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.Utils.*;

class DBFAnnotatedReader<T> implements DBFReader<T> {
    private final Class<T> type;
    private final DBFReader<DBFRow> baseReader;
    DBFAnnotatedReader(String dbfFileName, DBFOptions options, Class<T> type) {
        validateMetaDataDBF(type);
        baseReader = new DBF3BaseReader(dbfFileName,options);
        this.type = type;
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
    public List<T> readAllRows() {
        List<T> res = new ArrayList<>();
        List<DBFRow> rows = baseReader.readAllRows();
        for (DBFRow row:rows) res.add(covert(row));
        return res;
    }

    private T covert(DBFRow row) {
        T res = createInstance(type);
        DBFGenerateStrategies strategies = checkStrategy(type);
        switch(strategies){
            case AUTOMATIC: return fillByAutomaticStrategy(res,row);
            case ANNOTATED_FIELDS: return fillByAnnotatedFieldStrategy(res,row);
            case ANNOTATED_GETTERS: return fillByAnnotatedGetterFieldStrategy(res,row);
            case FIELDS_NAME: return fillByFieldNameStrategy(res,row);
            case GETTERS_NAME: return fillByGetterNameStrategy(res,row);
        }
        return res;
    }

    @Override
    public List<T> readByFilter(DBFFilter filter) {
        List<T> res = new ArrayList<>();
        List<DBFRow> rows = baseReader.readByFilter(filter);
        for (DBFRow row:rows){
            res.add(covert(row));
        }
        return res;
    }

    @Override
    public List<T> readByFilter(Set<DBFFilter> filters) {
        return null;
    }

    @Override
    public DBFField[] getFields() {
        return baseReader.getFields();
    }

    @Override
    public T readCurrentRow() {
        return null;
    }

    @Override
    public  List<T> readRange(long start, long end) {
        return null;
    }

    @Override
    public void close() {
        baseReader.close();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            final Iterator<DBFRow> iterator = baseReader.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return covert(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
}
