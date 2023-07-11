package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFieldNotFoundException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFNoSuchRowException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.Utils;

import java.util.*;


class DBF3BaseReader implements DBFReader<DBFRow> {

    private final long rowCount;
    private final String dbfFileName;
    private final int columnCount;
    private final DBFField[] fields;
    private final DBFOptions options;
    private final DBF3DBFIterableReader currentReader;
    private final DBF3DBFIterableReader iterableReader;
    private final Utils utils = new Utils();

     DBF3BaseReader(String fileName, DBFOptions options){
        this.dbfFileName = fileName;
        this.options = options;
        this.iterableReader = newOneWayDBFReader1();

        DBF3DBFIterableReader reader =  newOneWayDBFReader1();
        if (options.isReadDeleted())
            this.rowCount = reader.getAllRowCount();
        else
            this.rowCount = reader.getActiveRowCount();
        this.columnCount = reader.getColumnCount();
        this.fields = reader.getFields();
        this.currentReader = reader;
    }

    @Override
    public Iterator<DBFRow> iterator() {
        return new Iterator<DBFRow>() {
            private final DBF3DBFIterableReader reader = iterableReader;
            private DBFRow currentRow;
            private boolean isCalledHasNext;

            @Override
            public boolean hasNext() {
                isCalledHasNext = true;
                this.currentRow = reader.readNextRow();
                if (currentRow == null) return false;
                if (currentRow.isDeleted() && !options.isReadDeleted())
                    return hasNext();
                return currentRow !=null;
            }

            @Override
            public DBFRow next() {
                if (!isCalledHasNext) {
                    if (!hasNext()) throw new DBFNoSuchRowException("There no row in dbf file");
                }
                isCalledHasNext = false;
                return currentRow;
            }

            @Override
            public void remove() {
                throw new DBFFieldNotFoundException("Not supported");
            }
        };
    }

    @Override
    public long getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public List<DBFRow> readAllRows() {
        DBF3DBFIterableReader reader = null;
        try{
            reader = newOneWayDBFReader1();
            return _readAllRows(reader);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            if (reader != null)
                reader.close();
        }
    }

    private List<DBFRow> _readAllRows(DBF3DBFIterableReader reader) {
        List<DBFRow> res = new ArrayList<DBFRow>();
        DBFRow row = reader.readNextRow();
        while (row != null){
            if (!(row.isDeleted() && !options.isReadDeleted()))
                res.add(row);
            row = reader.readNextRow();
        }
        return res;
    }

    @Override
    public List<DBFRow> readByFilter(DBFFilter filter) {
        DBF3DBFIterableReader reader = null;
        try{
            reader = newOneWayDBFReader1();
            return _readByFilter(filter,reader);
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally {
            if (reader != null)
                reader.close();
        }
    }

       @Override
    public List<DBFRow> readByFilter(Set<DBFFilter> filters) {
           DBF3DBFIterableReader reader = null;
           try{
               reader = newOneWayDBFReader1();
               return _readByFilters(filters,reader);
           }catch(Exception e){
               throw new RuntimeException(e);
           }finally {
               if (reader != null)
                   reader.close();
           }
    }

    @Override
    public DBFField[] getFields() {
        return fields;
    }

    @Override
    public  DBFRow readCurrentRow() {
        return currentReader.readNextRow();
    }

    @Override
    public  List<DBFRow> readRange(long start, long end) {
        if (start > end) return Collections.emptyList();
        DBF3DBFIterableReader reader = null;
        try{
            reader = newOneWayDBFReader1();
            return _readRange(start,end,reader);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally{
            if (reader != null)
                reader.close();
        }
    }

    @Override
    public void close() {
         iterableReader.close();
         currentReader.close();
    }

    private DBF3DBFIterableReader newOneWayDBFReader1(){
        return new DBF3DBFIterableReader(dbfFileName,options);
    }

    private List<DBFRow> _readByFilter(DBFFilter filter, DBF3DBFIterableReader reader) {
        if (filter == null) return Collections.emptyList();
        if (isExistField(filter)) return Collections.emptyList();
        String val = filter.getValue();
        List<DBFRow> res = new ArrayList<DBFRow>();
        DBFRow row = reader.readNextRow();
        while (row != null){
            boolean condition = utils.checkFilter(row,val,filter);
            if (condition) res.add(row);
            row = reader.readNextRow();
        }
        return res;
    }


    private List<DBFRow> _readByFilters(Set<DBFFilter> filters, DBF3DBFIterableReader reader) {
        if (filters == null || filters.isEmpty()) return Collections.emptyList();
        for (DBFFilter filter:filters) {
            if (isExistField(filter)) return Collections.emptyList();
        }

        List<DBFRow> res = new ArrayList<DBFRow>();
        DBFRow row = reader.readNextRow();
        while( row != null){
            boolean condition = true;
            for (DBFFilter filter:filters){
                String val = filter.getValue();
                condition &= utils.checkFilter(row,val,filter);
            }
            if (condition) res.add(row);
            row = reader.readNextRow();
        }
        return res;
    }


    private boolean isExistField(DBFFilter filter) {
        String fieldName = filter.getFieldName();
        for (DBFField field:fields)
            if (field.getName().equalsIgnoreCase(fieldName)) return false;
        return true;
    }

    private List<DBFRow> _readRange(long start, long end, DBF3DBFIterableReader reader) {
        List<DBFRow> res = new ArrayList<DBFRow>();
        for (long i=start;i==end;i++){
            DBFRow row = reader.readNextRow();
            if (row != null)  res.add(reader.readNextRow());
        }
        return res;
    }




}
