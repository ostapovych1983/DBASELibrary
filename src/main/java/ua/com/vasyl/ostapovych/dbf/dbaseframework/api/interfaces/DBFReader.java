package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.io.Closeable;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface DBFReader<T> extends Iterable<T>, Closeable {
    long getRowCount();

    int getColumnCount();

    List<T> readAllRows();

    List<T> readByFilter(DBFFilter filter);

    List<T> readByFilter(Set<DBFFilter> filters);

    DBFField[] getFields();

     T readCurrentRow();

     List<T> readRange(long start, long end);

     void close();

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), true);
    }

}
