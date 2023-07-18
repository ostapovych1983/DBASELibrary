package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFileReadException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils;

import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static java.lang.System.arraycopy;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType.*;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils.*;

final class ReadUtils {



    public static  Object readValueFromBytes(byte[] rawValue, DBFField field,DBFCodePage dbfCodePage) {
        switch (field.getDbfType()){
            case CHARACTER:
            case UNKNOWN:
                if (isNullByteArray(rawValue)){
                    return null;
                }
                return new String (rawValue,dbfCodePage.getJavaCharSet()).trim();
            case DATE: return getDateFromBytes(rawValue);
            case FLOAT: return getFloatFromByteArray(rawValue);
            case NUMERIC: return getNumericFromByteArray(rawValue,field);
            case MEMO: return null;
            case LOGICAL: return getLogicalFromByteArray(rawValue);
        }
        return null;
    }

    public static byte[] readBytes(RandomAccessFile dataInput, int size) {
        if (dataInput == null) throw new IllegalArgumentException("Parameter dataInput cannot be a null");
        byte[] res = new byte[size];
        try {
            dataInput.readFully(res);
        } catch (IOException e) {
            throw new DBFFileReadException(e);
        }
        return res;
    }

    public static void readBytes(DataInput dataInput, int size) {
        if (dataInput == null) throw new IllegalArgumentException("Parameter dataInput cannot be a null");
        byte[] res = new byte[size];
        try {
            dataInput.readFully(res);
        } catch (IOException e) {
            throw new DBFFileReadException(e);
        }
    }

    public static  byte[] readBytes(byte[] mass, int startPosition, int count) {
        return Arrays.copyOfRange(mass,startPosition,startPosition+count);
    }

    public static  int readBytesAsInt(byte[] bytes) {
        if (bytes.length<4){
            byte [] filledZeroArrays = new byte[4];
            arraycopy(bytes, 0, filledZeroArrays, 0, bytes.length);
            return ByteBuffer.wrap(filledZeroArrays).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }if (bytes.length>4){
            byte [] fixedMas = new byte[4];
            arraycopy(bytes, 0, fixedMas, 0, 4);
            return ByteBuffer.wrap(fixedMas).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static  void skipBytes(RandomAccessFile dataInput) {
        try {
            dataInput.skipBytes(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static  char readAsChar(byte b) {
        return (char) b;
    }

    public static  byte[] trimBytes(byte[] readBytes) {
        int size = 0;
        for (byte b:readBytes) if (b!=0x00) size++;
        byte [] res = new byte[size];
        int index = 0;
        for (byte b:readBytes) if (b!=0x00) res[index++] = b;
        return res;
    }

    public static  DBFType recognizeType(char rawType) {
        switch (rawType){
            case 'C' : return CHARACTER;
            case 'M' : return DBFType.MEMO;
            case 'N' : return DBFType.NUMERIC;
            case 'D' : return DATE;
            case 'F' : return DBFType.FLOAT;
            case 'L' : return LOGICAL;
        }
        return DBFType.UNKNOWN;
    }



    public static  boolean checkFilter(DBFRow row, String val, DBFFilter filter) {
        if (row == null) return false;
        Set<DBFField> fields = row.fields();
        DBFField field = null;
        for (DBFField f:fields){
            if (f.getName().equalsIgnoreCase(filter.getFieldName())){
                field = f;
                break;
            }
        }
        if (field == null) return false;
        switch (field.getDbfType()){
            case LOGICAL: return _checkLogicalFilter(row,val,field,filter);
            case NUMERIC: return _checkNumericFilter(row,val,field,filter);
            case FLOAT: return _checkFloatFilter(row,val,field,filter);
            case CHARACTER: return _checkCharacterFilter(row,val,field,filter);
            case DATE: return _checkDateField(row,val,field,filter);
        }
        return false;
    }

    public static  <T> T createInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static  <T> T fillByAutomaticStrategy(T t, DBFRow row) {
        if (t == null || row == null) return null;
        Field[] fields = removeStaticField(t.getClass().getDeclaredFields());
        Method[] methods = removeStaticMethods(t.getClass().getDeclaredMethods());
        boolean isOneGetterMethodContainsAnnotation = checkGettersOnAnnotation(methods);
        boolean isOneFieldContainsAnnotation = checkFieldsOnAnnotation(fields);

        if (isOneFieldContainsAnnotation && isOneGetterMethodContainsAnnotation){
            _fillByFieldMethod(fields,row,t);
            _fillByGettersMethod(methods,row,t);
            return t;
        }

        if (!isOneGetterMethodContainsAnnotation && isOneFieldContainsAnnotation){
            _fillByFieldMethod(fields,row,t);
            return t;
        }
        if (isOneGetterMethodContainsAnnotation){
            _fillByGettersMethod(methods,row,t);
            return t;
        }

        for (Method method:methods){
            boolean methodIsSetter = isMethodSetter(method,t);
            if (methodIsSetter){
                String fieldName = method.getName().substring(3,4).toLowerCase()+method.getName().substring(4);
                if (row.isContainField(fieldName)){
                    Object val = row.getAsObject(fieldName);
                    DBFUtils.invoke(method,t,val);
                }

            }
        }
        return t;
    }

    public static  <T> T fillByAnnotatedFieldStrategy(T t, DBFRow row) {
        Field[] fields = removeStaticField(t.getClass().getDeclaredFields());
        _fillByFieldMethod(fields,row,t);
        return t;
    }

    public static  <T> T fillByAnnotatedGetterFieldStrategy(T t, DBFRow row) {
        Method[] methods = removeStaticMethods(t.getClass().getDeclaredMethods());
        _fillByGettersMethod(methods,row,t);
        return t;
    }

    public static  <T> T fillByFieldNameStrategy(T t, DBFRow row) {
        Field [] fields = t.getClass().getDeclaredFields();
        for (Field field:fields){
            if (row.isContainField(field.getName())){
                setValueToField(field,t,row.getAsObject(field.getName()));
            }
        }
        return t;
    }

    public static <T> T fillByGetterNameStrategy(T t, DBFRow row) {
        Method [] methods = removeStaticMethods(t.getClass().getDeclaredMethods());
        for (Method method:methods){
            if (isMethodSetter(method,t)) {
                String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                if (row.isContainField(fieldName)) {
                    DBFUtils.invoke(method, t, row.getAsObject(fieldName));
                }
            }
        }
        return t;
    }

    static private Boolean  getLogicalFromByteArray(byte[] rawValue) {
        byte b = rawValue[0];
        if (b == 84) return true;
        if (b == 70) return false;
        if (b == 0) return false;
        throw new DBFIllegalValueException("Cannot set value "+rawValue[0]+ " to Boolean. " +
                "Value Should be "+(byte)84 +" or "+ (byte)70);
    }

    static private Number getNumericFromByteArray(byte[] rawValue, DBFField field) {
        int decimalSize = field.getDecimalSize();
        String valAsString = new String(rawValue).trim();
        if (valAsString.isEmpty()) return 0;
        try {
            if (decimalSize == 0) return Integer.parseInt(valAsString);
            return Double.parseDouble(valAsString);
        }catch (NumberFormatException e){
            if (decimalSize == 0) return Integer.parseInt("0");
            else return Double.parseDouble("0.0");
        }
    }

    static private Float getFloatFromByteArray(byte[] rawValue) {
        String valAsString = new String(rawValue).trim();
        if (valAsString.isEmpty()) return 0.0f;
        try {
            return Float.parseFloat(valAsString);
        }catch (NumberFormatException e){
            return 0.0f;
        }
    }

    static private Date getDateFromBytes(byte[] rawValue) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            if (isNullByteArray(rawValue)){
                return null;
            }
            String dateAsString = new String(rawValue);
            if (dateAsString.isEmpty()){
                return null;
            }
            return simpleDateFormat.parse(new String(rawValue));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isNullByteArray(byte[] bytes) {
        if (bytes == null){
            return true;
        }
        for (byte b:bytes){
            if (b != 0) return false;
        }
        return true;
    }




    private static boolean _checkLogicalFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()){
            case EQUAL: return row.getAsLogic(filteredField) == Boolean.parseBoolean(val);
            case NOT_EQUAL: return row.getAsLogic(filteredField) != Boolean.parseBoolean(val);
        }
        return false;
    }



    private static  boolean _checkNumericFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()) {
            case EQUAL:   {
                if (filteredField.getDecimalSize() > 0)
                    return Double.parseDouble(val) == row.getAsDouble(filteredField);
                else
                    return Integer.parseInt(val) == row.getAsInteger(filteredField);
            }
            case NOT_EQUAL: {
                if (filteredField.getDecimalSize() > 0)
                    return Double.parseDouble(val) != row.getAsDouble(filteredField);
                else
                    return Integer.parseInt(val) != row.getAsInteger(filteredField);
            }

            case LESS: {
                if (filteredField.getDecimalSize() > 0)
                    return  row.getAsDouble(filteredField)  <  Double.parseDouble(val);
                else
                    return row.getAsInteger(filteredField) < Integer.parseInt(val);
            }
            case MORE_THAN: {
                if (filteredField.getDecimalSize() > 0)
                    return  row.getAsDouble(filteredField)  >  Double.parseDouble(val);
                else
                    return row.getAsInteger(filteredField) > Integer.parseInt(val);
            }
            case CONTAINS:{
                return  row.getAsString(filteredField).contains(val);
            }
            case LIKE:{
                return row.getAsString(filteredField).matches(val.replace('%','*'));
            }
            case NOT_LIKE:{
                return !row.getAsString(filteredField).matches(val.replace('%','*'));
            }
        }
        return false;
    }

    private static  boolean _checkFloatFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()) {
            case EQUAL: return Float.parseFloat(val) == row.getAsFloat(filteredField);
            case NOT_EQUAL: return Float.parseFloat(val) != row.getAsFloat(filteredField);
            case LESS: return  row.getAsFloat(filteredField) < Float.parseFloat(val);
            case MORE_THAN:return  row.getAsFloat(filteredField) > Float.parseFloat(val);
            case CONTAINS: return  row.getAsString(filteredField).contains(val);
            case LIKE:  return row.getAsString(filteredField).matches(val.replace('%','*'));
            case NOT_LIKE: return !row.getAsString(filteredField).matches(val.replace('%','*'));
        }
        return false;
    }

    private static boolean _checkCharacterFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()){
            case EQUAL: return val.equalsIgnoreCase(row.getAsString(filteredField));
            case NOT_EQUAL: return !val.equalsIgnoreCase(row.getAsString(filteredField));
            case CONTAINS: return  row.getAsString(filteredField).contains(val);
            case LIKE:   return row.getAsString(filteredField).matches(val.replace('%','*'));
            case NOT_LIKE: return !row.getAsString(filteredField).matches(val.replace('%','*'));
        }
        return false;
    }

    private static boolean _checkDateField(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        try {
            switch (filter.getOperation()){
                case EQUAL:return new SimpleDateFormat("dd.MM.yyy").parse(val).equals(row.getAsDate(filteredField));
                case NOT_EQUAL: return !new SimpleDateFormat("dd.MM.yyy").parse(val).equals(row.getAsDate(filteredField));

            }
            return false;
        } catch (ParseException e) {
            return false;
        }
    }

    private static <T> void _fillByFieldMethod(Field [] fields,DBFRow row, T t){
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class)){
                DBFColumn annotation = field.getAnnotation(DBFColumn.class);
                Object val = row.getAsObject(annotation.name());
                setValueToField(field,t,val);
            }
        }
    }
    private static <T> void _fillByGettersMethod(Method[] methods,DBFRow row, T t){
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class)) {
                DBFColumn annotation = method.getAnnotation(DBFColumn.class);
                Object val = row.getAsObject(annotation.name());
                Method setterMethod = getSetterMethod(method,t);
                if (setterMethod != null) DBFUtils.invoke(setterMethod, t, val);
                else throw new RuntimeException("Getter not found");
            }
        }
    }

    private static <T> boolean isMethodSetter(Method method,T t) {
        boolean isStartFromSetWord = method.getName().startsWith("set");
        if (!isStartFromSetWord) return false;
        boolean hasOneParameter = method.getParameterTypes().length == 1;
        if (!hasOneParameter) return false;
        String purposedFieldName =
                method.getName().substring(3,4).toLowerCase()+method.getName().substring(4);
        Class<?> tClass = t.getClass();
        boolean isContainField = false;
        for (Field field : tClass.getDeclaredFields()){
            if (field.getName().equals(purposedFieldName)){
                isContainField = true;
                break;
            }
        }
        return isContainField;
    }

    private static <T> void setValueToField(Field field, T t,Object val) {
        field.setAccessible(true);
        try {
            field.set(t,val);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Method getSetterMethod(Method method,T t) {
        String methodName = method.getName();
        String setterMethodName = methodName.replace("get","set");
        Class<?> tClass = t.getClass();
        for (Method m:tClass.getDeclaredMethods()){
            if (m.getName().equals(setterMethodName)) return m;
        }
        return null;
    }

}
