package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFMap;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.*;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.Utils;

import java.io.*;

class DBF3DBFIterableReader implements Closeable {
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
    private final Utils utils;
    private final DBFOptions dbfOptions;

    DBF3DBFIterableReader(String dbfFiledName, DBFOptions options){
        RandomAccessFile dataInput;
        this.dbfOptions = options;
        utils = new Utils();
        this.dbfCodePage = options.getCodePage();
        try {
            file = new File(dbfFiledName);
            dataInput = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            throw new DBFFileOpenException("Cannot open file  " + dbfFiledName+" check if file exist and you have access to him", e);
        }
        readHeader(dataInput);
        activeRowCount = -1;
    }

    public DBFRow readNextRow() {
        if (allRowCount <= currentRow) return null;
        byte[] row = utils.readBytes(dataInput, rowSize);

        DBFMap dbfMap = new DBFMap();
        int startReader = 1;

        for (int i=0;i<columnCount;i++) {
            DBFField dbfField = fields[i];
            int sizeFieldOfRow = dbfField.getSize();
            byte[] fieldVal = utils.readBytes(row, startReader, sizeFieldOfRow);
            Object value = utils.readValueFromBytes(fieldVal, dbfField, dbfCodePage);
            dbfMap.put(dbfField,value);
            startReader += sizeFieldOfRow;
        }
        currentRow++;
        return new DBFRowImpl(dbfMap, row[0] == 0x2A);
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
        byte [] caption = utils.readBytes(dataInput,32);
        allRowCount = utils.readBytesAsInt(utils.readBytes(caption,4,4));
        headerSize = utils.readBytesAsInt(utils.readBytes(caption,8,2));
        this.rowSize = utils.readBytesAsInt(utils.readBytes(caption,10,11));
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
        byte [] filedCaptions = utils.readBytes(dataInput,headerSize-32-1);
        fields = generateFieldIndexInFile(filedCaptions);
        utils.skipBytes(dataInput); // skip header terminator byte.
        this.columnCount = (headerSize-32-1)/32;
    }


    private DBFField[] generateFieldIndexInFile(byte[] allRowsBytes) {
        DBFField[] res = new DBFField[allRowsBytes.length/32];
        int index = 0;
        int start = 0;
        while(start <allRowsBytes.length) {
            byte[] fieldBytes = utils.readBytes(allRowsBytes,start,32);
            String fieldName = new String(utils.trimBytes(utils.readBytes(fieldBytes,0,11)));
            char rawType = utils.readAsChar(fieldBytes[11]);
            DBFType dbfType = utils.recognizeType(rawType);
            short fieldSize = (short) utils.readBytesAsInt(new byte[]{fieldBytes[16]});
            short decimalSize = (short) utils.readBytesAsInt(new byte[]{fieldBytes[17]});
            DBFField field = utils.generateDBFField(fieldName,dbfType,fieldSize,decimalSize);
            res[index] = field;
            index++;
            start += 32;
        }
        return res;
    }

    private long calculateActiveRowCount() {
        RandomAccessFile activeRowCountDataInput = null;
        try{
            activeRowCountDataInput = new RandomAccessFile(file,"r");
            return _calculateActiveRowCount(activeRowCountDataInput);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            if (activeRowCountDataInput != null) {
                try {
                    activeRowCountDataInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private long _calculateActiveRowCount(DataInput dataInput) {
        long res = 0;
        utils.readBytes(dataInput,headerSize);
        while (true){
            try {
                byte[] ignore = new byte[rowSize];
                dataInput.readFully(ignore);
                if (ignore[0] == 0x20) res++;
            } catch (EOFException e) {
                //it is ok! eof means there no row in dbf and now we can return counted row.
                return res;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        if (dataInput != null){
            try {
                dataInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}