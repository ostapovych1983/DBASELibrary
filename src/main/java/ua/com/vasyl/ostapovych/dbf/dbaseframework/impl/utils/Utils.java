package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.io.DataInput;
import java.io.RandomAccessFile;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies.AUTOMATIC;

public class Utils {
    private final __ByteArrayUtils__ byteArrayUtils = new __ByteArrayUtils__();
    private final __FilterUtils__ filterUtils = new __FilterUtils__();
    private final __DBFUtils__ dbfUtils = new __DBFUtils__();
    private final __ReflectionUtils__ reflectionUtils = new __ReflectionUtils__();
    private final __AccessorsUtils__ accessorsUtils = new __AccessorsUtils__();

    public Object readValueFromBytes(byte[] rawValue, DBFField field,DBFCodePage dbfCodePage) {
        return byteArrayUtils.readValueFromBytes(rawValue,field,dbfCodePage);
    }

    public byte[] readBytes(RandomAccessFile dataInput, int rowSize) {
        return byteArrayUtils.readBytes(dataInput,rowSize);
    }

    public void readBytes(DataInput dataInput, int rowSize) {
        byteArrayUtils.readBytes(dataInput, rowSize);
    }

    public byte[] readBytes(byte[] row, int startReader, int sizeFieldOfRow) {
        return byteArrayUtils.readBytes(row,startReader,sizeFieldOfRow);
    }

    public int readBytesAsInt(byte[] readBytes) {
        return byteArrayUtils.readBytesAsInt(readBytes);
    }

    public void skipBytes(RandomAccessFile dataInput) {
        byteArrayUtils.skipBytes(dataInput);
    }

    public char readAsChar(byte fieldByte) {
        return byteArrayUtils.readAsChar(fieldByte);
    }

    public byte[] trimBytes(byte[] readBytes) {
        return byteArrayUtils.trimBytes(readBytes);
    }

    public DBFType recognizeType(char rawType) {
        return dbfUtils.recognizeType(rawType);
    }

    public DBFField generateDBFField(String fieldName, DBFType dbfType, short fieldSize, short decimalSize) {
        return dbfUtils.generateDBFField(fieldName, dbfType, fieldSize, decimalSize);
    }

    public <T> DBFField[] generateFields(Class<T> tClass, DBFGenerateStrategies strategy) {
        return dbfUtils.generateFields(tClass, strategy);
    }

    public byte[] createCaption(DBFField[] fields, int size) {
        return byteArrayUtils.createCaption(fields,size);
    }

    public <T> byte[] toDBFRecord(DBFField[] fields, T row) {
        return byteArrayUtils.toDBFRecord(fields,row);
    }

    public boolean checkFilter(DBFRow row, String val, DBFFilter filter) {
        return filterUtils.checkFilter(row,val,filter);
    }

    public byte[] toDBFRecord(DBFField [] fields,Object[] row) {
        return byteArrayUtils.toDBFRecord(fields,row);
    }

    public <T> T createInstance(Class<T> type) {
        return reflectionUtils.createInstance(type);
    }

    public DBFGenerateStrategies checkStrategy(Class<?> tClass) {
        if (!tClass.isAnnotationPresent(DBFTable.class)) return AUTOMATIC;
        DBFTable table = tClass.getAnnotation(DBFTable.class);
        return table.accessMode();
    }


    public <T> T fillByAutomaticStrategy(T t, DBFRow row) {
        return accessorsUtils.fillByAutomaticStrategy(t,row);
    }

    public <T> T fillByAnnotatedFieldStrategy(T t, DBFRow row) {
        return accessorsUtils.fillByAnnotatedFieldStrategy(t,row);
    }

    public <T> T fillByAnnotatedGetterFieldStrategy(T t, DBFRow row) {
        return accessorsUtils.fillByAnnotatedGetterFieldStrategy(t,row);
    }

    public <T> T fillByFieldNameStrategy(T t, DBFRow row) {
        return accessorsUtils.fillByFieldNameStrategy(t,row);
    }

    public <T> T fillByGetterNameStrategy(T res, DBFRow row) {
        return accessorsUtils.fillByGetterNameStrategy(res,row);
    }
}
