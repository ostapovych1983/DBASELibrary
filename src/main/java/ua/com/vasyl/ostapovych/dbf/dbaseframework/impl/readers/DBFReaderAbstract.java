package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers;


import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFMap;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFieldNotFoundException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFNoSuchRowException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLogger;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLoggerManager;

import java.io.Closeable;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers.ReadUtils.*;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils.generateDBFField;


abstract class DBFReaderAbstract<T> implements DBFReader<T> {

    private final DBFLogger logger;
    private final long rowCount;
    private final String dbfFileName;
    private final int columnCount;
    private final DBFField[] fields;
    private final DBFOptions options;
    private final DBF3DBFIterableReader currentReader;
    private final DBF3DBFIterableReader iterableReader;

    DBFReaderAbstract(String fileName, DBFOptions options,DBFLogger logger){
        this.logger = logger;
        logger.debug("Creating %s with options {}", getClass().getSimpleName(), options);
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
    public Iterator<T> iterator() {
        logger.trace("Getting DBF iterator");
        return new Iterator<T>() {
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
            public T next() {
                if (!isCalledHasNext) {
                    if (!hasNext()) throw new DBFNoSuchRowException("There no row in dbf file");
                }
                isCalledHasNext = false;
                return convert(currentRow);
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
    public List<T> readAllRows() {
        try (DBF3DBFIterableReader reader = newOneWayDBFReader1()) {
            return readAllRowsFromReader(reader);
        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);
            logger.error("Error when reading row. Error = {}}",e.getMessage());
            throw runtimeException;
        }
    }

    protected abstract T convert(DBFRow row);

    private List<T> readAllRowsFromReader(DBF3DBFIterableReader reader) {
        List<T> res = new ArrayList<>();
        DBFRow row = reader.readNextRow();
        while (row != null){
            if (!(row.isDeleted() && !options.isReadDeleted()))
                res.add(convert(row));
            row = reader.readNextRow();
        }
        return res;
    }

    @Override
    public List<T> readByFilter(DBFFilter filter) {
        try (DBF3DBFIterableReader reader = newOneWayDBFReader1()) {
            return readByFilterFromReader(filter, reader);
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(e);
            logger.error("Error read data by filter %s. Error = {}}",filter,e.getMessage());
            throw exception;
        }
    }

    @Override
    public List<T> readByFilter(Set<DBFFilter> filters) {
        try (DBF3DBFIterableReader reader = newOneWayDBFReader1()) {
            return readByFiltersFromReader(filters, reader);
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(e);
            logger.error("Error read data by filter %s. Error = '%s'",filters,e.getMessage());
            throw exception;
        }
    }

    @Override
    public DBFField[] getFields() {
        return fields;
    }

    @Override
    public  T readCurrentRow() {
        return convert(currentReader.readNextRow());
    }

    @Override
    public  List<T> readRange(long start, long end) {
        if (start > end) return Collections.emptyList();
        try (DBF3DBFIterableReader reader = newOneWayDBFReader1()) {
            return readRangeFromFile(start, end, reader);
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(e);
            logger.error("Error read data from %d to %d. Error = '%s'",start,end,e.getMessage());
            throw exception;
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

    private List<T> readByFilterFromReader(DBFFilter filter, DBF3DBFIterableReader reader) {
        if (filter == null) return Collections.emptyList();
        if (isExistField(filter)) return Collections.emptyList();
        String val = filter.getValue();
        List<T> res = new ArrayList<>();
        DBFRow row = reader.readNextRow();
        while (row != null){
            boolean condition = checkFilter(row,val,filter);
            if (condition) res.add(convert(row));
            row = reader.readNextRow();
        }
        return res;
    }


    private List<T> readByFiltersFromReader(Set<DBFFilter> filters, DBF3DBFIterableReader reader) {
        if (filters == null || filters.isEmpty()) return Collections.emptyList();
        for (DBFFilter filter:filters) {
            if (isExistField(filter)) return Collections.emptyList();
        }

        List<T> res = new ArrayList<>();
        DBFRow row = reader.readNextRow();
        while( row != null){
            boolean condition = true;
            for (DBFFilter filter:filters){
                String val = filter.getValue();
                condition &= checkFilter(row,val,filter);
            }
            if (condition) res.add(convert(row));
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

    private List<T> readRangeFromFile(long start, long end, DBF3DBFIterableReader reader) {
        List<T> res = new ArrayList<>();
        for (long i=start;i==end;i++){
            DBFRow row = reader.readNextRow();
            if (row != null)  res.add(convert(reader.readNextRow()));
        }
        return res;
    }

    private static class DBF3DBFIterableReader implements Closeable {
        private RandomAccessFile dataInput;
        private long allRowCount;
        private int headerSize;
        private int rowSize;
        private DBFField [] fields;
        private int currentRow = 0;
        private DBFCodePage dbfCodePage;
        private int columnCount;
        private long activeRowCount;
        private final File file;
        private final DBFOptions dbfOptions;
        private final DBFLogger logger;

        DBF3DBFIterableReader(String dbfFiledName, DBFOptions options){
            String loggerName = getClass().getSimpleName();
            logger = DBFLoggerManager.getDBFLogger(loggerName);
            RandomAccessFile dataInput;
            this.dbfOptions = options;
            this.dbfCodePage = options.getCodePage();
            try {
                file = new File(dbfFiledName);
                dataInput = new RandomAccessFile(file, "r");
            } catch (Exception e) {
                logger.error("Cannot open file %s check if file exist and you have access to him. Error = '%s'"
                        ,dbfFiledName,e.getMessage());
                throw new RuntimeException(
                        String.format("Cannot open file %s check if file exist and you have access to him",
                                dbfFiledName), e);
            }
            readHeader(dataInput);
            activeRowCount = -1;
        }

        public DBFRow readNextRow() {
            if (allRowCount <= currentRow) return null;
            byte[] row = readBytes(dataInput, rowSize);

            DBFMap dbfMap = new DBFMap();
            int startReader = 1;

            for (int i=0;i<columnCount;i++) {
                DBFField dbfField = fields[i];
                int sizeFieldOfRow = dbfField.getSize();
                byte[] fieldVal = readBytes(row, startReader, sizeFieldOfRow);
                Object value = readValueFromBytes(fieldVal, dbfField, dbfCodePage);
                dbfMap.put(dbfField,value);
                startReader += sizeFieldOfRow;
            }
            currentRow++;
            return new DBFRow(dbfMap, row[0] == 0x2A);
        }

        public long getAllRowCount(){
            return allRowCount;
        }

        public int getColumnCount() {
            return columnCount;
        }

        public long getActiveRowCount() {
            if (activeRowCount <0)
                this.activeRowCount = calculateActiveRowCount();
            return activeRowCount;
        }

        public DBFField[] getFields() {
            return fields;
        }

        private void readHeader(RandomAccessFile dataInput) {
            this.dataInput = dataInput;
            if (dataInput == null) throw new IllegalArgumentException("parameter dataInput cannot be a null");
            byte [] caption = readBytes(dataInput,32);
            allRowCount = readBytesAsInt(readBytes(caption,4,4));
            headerSize = readBytesAsInt(readBytes(caption,8,2));
            this.rowSize = readBytesAsInt(readBytes(caption,10,11));
            byte codePage = caption[29];
            if (dbfOptions.isReadCodePageFromTable()){
                DBFCodePage tableCodePage = DBFCodePage.readByCode(codePage);
                if (tableCodePage == DBFCodePage.NONE){
                    dbfCodePage = dbfOptions.getCodePage();
                }else{
                    this.dbfCodePage = tableCodePage;
                }
            }else {
                dbfCodePage = dbfOptions.getCodePage();
            }
            byte [] filedCaptions = readBytes(dataInput,headerSize-32-1);
            fields = generateFieldIndexInFile(filedCaptions);
            skipBytes(dataInput); // skip header terminator byte.
            this.columnCount = (headerSize-32-1)/32;
        }


        private DBFField[] generateFieldIndexInFile(byte[] allRowsBytes) {
            DBFField[] res = new DBFField[allRowsBytes.length/32];
            int index = 0;
            int start = 0;
            while(start <allRowsBytes.length) {
                byte[] fieldBytes = readBytes(allRowsBytes,start,32);
                String fieldName = new String(trimBytes(readBytes(fieldBytes,0,11)));
                char rawType = readAsChar(fieldBytes[11]);
                DBFType dbfType = recognizeType(rawType);
                short fieldSize = (short) readBytesAsInt(new byte[]{fieldBytes[16]});
                short decimalSize = (short) readBytesAsInt(new byte[]{fieldBytes[17]});
                DBFField field = generateDBFField(fieldName,dbfType,fieldSize,decimalSize);
                res[index] = field;
                index++;
                start += 32;
            }
            return res;
        }

        private long calculateActiveRowCount() {
            try (RandomAccessFile activeRowCountDataInput = new RandomAccessFile(file, "r")) {
                return calculateActiveRowCountFromDataInput(activeRowCountDataInput);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private long calculateActiveRowCountFromDataInput(DataInput dataInput) {
            long res = 0;
            readBytes(dataInput,headerSize);
            while (true){
                try {
                    byte[] ignore = new byte[rowSize];
                    dataInput.readFully(ignore);
                    if (ignore[0] == 0x20) res++;
                } catch (EOFException e) {
                    //it is ok! eof means there no row in dbf, and now we can return counted row.
                    return res;
                } catch (IOException e) {
                    RuntimeException exception = new RuntimeException(e);
                    logger.error("Error occurred when try to detect row size. Error = {}}",e.getMessage());
                    throw exception;
                }
            }
        }

        @Override
        public void close() {
            if (dataInput != null){
                try {
                    dataInput.close();
                } catch (IOException e) {
                    //NOP
                }
            }
        }
    }
}
