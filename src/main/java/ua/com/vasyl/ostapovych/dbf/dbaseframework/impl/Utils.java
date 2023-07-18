package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFileReadException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalFieldException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;


import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.arraycopy;
import static java.lang.reflect.Modifier.isStatic;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies.AUTOMATIC;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType.*;

abstract class Utils {

    private static final String DBF_DATE_FORMAT = "yyyyMMdd";

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

    public static  DBFField generateDBFField(String fieldName, DBFType type, short size, short decimalCount) {
        switch (type){
            case LOGICAL: return new LogicalField(fieldName);
            case DATE: return new DateField(fieldName);
            case CHARACTER: return new CharacterField(fieldName,size);
            case NUMERIC: return new NumericField(fieldName,size,decimalCount);
            case FLOAT: return new FloatField(fieldName,size,decimalCount);
        }
        return new UnknownField(fieldName);
    }

    public static  <T> DBFField[] generateFields(Class<T> tClass, DBFGenerateStrategies strategy) {
        switch (strategy){
            case AUTOMATIC: return _automaticStrategy(tClass);
            case ANNOTATED_FIELDS: return _annotatedFieldsStrategy(tClass);
            case ANNOTATED_GETTERS: return _annotatedGetterStrategy(tClass);
            case FIELDS_NAME: return _byFieldNameStrategy(tClass);
            case GETTERS_NAME: return _byGettersNamedStrategy(tClass);
        }
        return new DBFField[0];
    }

    public static  byte[] createCaption(DBFField[] fields, int size) {
        short captionSize = (short) (32+(32*fields.length)+1);
        byte [] caption = new byte[captionSize];
        caption[0] = 0x3;
        byte monthLastUpdate = _getFromDate(Calendar.MONTH);
        byte dayOfLastUpdate = _getFromDate(Calendar.DAY_OF_MONTH);
        byte yearLastUpdate = _getFromDate(Calendar.YEAR);
        caption[1] = monthLastUpdate;
        caption[2] = dayOfLastUpdate;
        caption[3] = yearLastUpdate;
        byte[] recordCount = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(size).array();
        arraycopy(recordCount, 0, caption, 4, recordCount.length);
        byte[] headerSize = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(captionSize).array();
        arraycopy(headerSize,0,caption,8,headerSize.length);
        short recordSize = calculateRecordSize(fields);
        byte[] recordSizeAsByteArray = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(recordSize).array();
        arraycopy(recordSizeAsByteArray,0,caption,10,recordSizeAsByteArray.length);
        caption[29] = 0x65;
        byte[] fieldDefinitionAsByteArray = getFieldDefinition(fields);
        arraycopy(fieldDefinitionAsByteArray,0,caption,32,fieldDefinitionAsByteArray.length);
        return caption;
    }

    public  static  <T> byte[] toDBFRecord(DBFField[] fields, T row) {
        byte[] res = new byte[calculateRecordSize(fields)];
        res[0] = 0x20;
        int currentPositionRes = 1;
        for (DBFField field : fields) {
            byte[] fieldAsByteArray = toDBFFieldAsByteArray(field, row);
            arraycopy(fieldAsByteArray, 0, res, currentPositionRes, fieldAsByteArray.length);
            currentPositionRes+=field.getSize();
        }
        return res;
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

    public static  byte[] toDBFRecord(DBFField [] fields,Object[] row) {
        if (fields.length > row.length) throw new IllegalArgumentException("Count of row cannot be less than count of dbfFields");
        byte[] res = new byte[calculateRecordSize(fields)];
        res[0] = 0x20;
        int currentPositionRes = 1;
        for (int i=0;i< fields.length;i++){
            DBFField field = fields[i];
            byte[] fieldAsByteArray = toDBFFieldAsByteArray(field, row, i);
            arraycopy(fieldAsByteArray, 0, res, currentPositionRes, fieldAsByteArray.length);
            currentPositionRes+=field.getSize();
        }
        return res;
    }

    public static  <T> T createInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static  DBFGenerateStrategies checkStrategy(Class<?> tClass) {
        if (!tClass.isAnnotationPresent(DBFTable.class)) return AUTOMATIC;
        DBFTable table = tClass.getAnnotation(DBFTable.class);
        return table.accessMode();
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
                    invoke(method,t,val);
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
                    invoke(method, t, row.getAsObject(fieldName));
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

    private static <T> DBFField[] _automaticStrategy(Class<T> tClass) {
        Field[] fields = removeStaticField(tClass.getDeclaredFields());
        Method[] methods = removeNotGetterMethods(removeStaticMethods(tClass.getDeclaredMethods()));
        boolean isOneGetterMethodContainsAnnotation = checkGettersOnAnnotation(methods);
        boolean isOneFieldContainsAnnotation = checkFieldsOnAnnotation(fields);

        if (isOneFieldContainsAnnotation && isOneGetterMethodContainsAnnotation){
            return _getDBFFieldsAndGettersWithAnnotation(fields,methods);
        }

        if (!isOneGetterMethodContainsAnnotation && isOneFieldContainsAnnotation){
            return _getDBFFieldsByAnnotationFields(fields);
        }
        if (isOneGetterMethodContainsAnnotation){
            return _getDBFFieldsByAnnotationGetters(methods);
        }

        return _getDBFFieldsWithoutAnnotation(methods);
    }

    private static DBFField[] _getDBFFieldsAndGettersWithAnnotation(Field[] fields, Method[] methods) {
        List<DBFField> res = new ArrayList<>();
        methods = removeNotGetterMethods(methods);
        Method [] annotatedMethods = orderAnnotatedMethods(getAnnotatedMethods(methods));
        Field [] annotatedFields = orderAnnotatedFields(getAnnotatedFields(fields));
        for (Method method:annotatedMethods){
            res.add(generateFieldByAnnotationMethod(method));
        }
        for (Field field:annotatedFields){
            if (!hasAnnotatedGetter(field,annotatedMethods)){
                res.add(generateDBFField(field.getAnnotation(DBFColumn.class)));
            }
        }
        Map<Short,DBFField> orderedMap = new TreeMap<>();
        for (Method method: annotatedMethods){
            orderedMap.put(
                    method.getAnnotation(DBFColumn.class).positionOnDBFFile(),
                    fieldAnnotationMapping(method.getAnnotation(DBFColumn.class),res)
            );
        }
        for (Field field: annotatedFields){
            DBFField dbfField = fieldAnnotationMapping(field.getAnnotation(DBFColumn.class),res);
            if (dbfField != null)
                orderedMap.put(field.getAnnotation(DBFColumn.class).positionOnDBFFile(), dbfField );
        }
        return orderedMap.values().toArray(new DBFField[0]);
    }

    private static DBFField generateFieldByAnnotationMethod(Method method) {
        DBFColumn annotation =
                method.getAnnotation(DBFColumn.class);
        return generateDBFField(annotation);
    }

    private static DBFField generateDBFField(DBFColumn annotation){
        String fieldName = annotation.name();
        DBFType type = annotation.type();
        switch (type){
            case CHARACTER: return new CharacterField(fieldName,annotation.size());
            case DATE: return new DateField(fieldName);
            case FLOAT: return new FloatField(fieldName,annotation.size(), annotation.decimalSize());
            case LOGICAL:return new LogicalField(fieldName);
            case NUMERIC:return new NumericField(annotation.name(),annotation.size(),annotation.decimalSize());
        }
        return null;
    }

    private static <T> DBFField[] _annotatedFieldsStrategy(Class<T> tClass) {
        Field[] fields = getAnnotatedFields(removeStaticField(tClass.getDeclaredFields()));
        List<DBFField> res = new ArrayList<>();
        for (Field field:fields){
            res.add(generateDBFField(field.getAnnotation(DBFColumn.class)));
        }
        return res.toArray(new DBFField[0]);
    }

    private static <T> DBFField[] _annotatedGetterStrategy(Class<T> tClass) {
        Method [] getters = getAnnotatedMethods(
                removeStaticMethods(
                        removeNotGetterMethods(tClass.getMethods())));
        getters = orderAnnotatedMethods(getters);
        List<DBFField> res = new ArrayList<>();
        for (Method method:getters){
            res.add(generateFieldByAnnotationMethod(method));
        }
        return res.toArray(new DBFField[0]);
    }

    private static <T> DBFField[] _byFieldNameStrategy(Class<T> tClass) {
        Field[] fields = removeStaticField(tClass.getDeclaredFields());
        List<DBFField> res = new ArrayList<>();
        for (Field field:fields){
            res.add(generateDBFFieldFromClassField(field));
        }
        return res.toArray(new DBFField[0]);
    }

    private static DBFField generateDBFFieldFromClassField(Field field) {
        String fileName = field.getName();
        DBFType dbfType = generateDBFType(field.getType());
        if (dbfType == null)
            throw new DBFIllegalFieldException(String.format("Cannot mapping dbf field type %s", field.getType()));
        return generateDBFField(fileName,dbfType,field.getType());
    }

    private static <T> DBFType generateDBFType(Class<T> type) {
        if (Float.class.equals(type)) {
            return DBFType.FLOAT;
        } else if (Double.class.equals(type)) {
            return DBFType.NUMERIC;
        } else if (Integer.class.equals(type)) {
            return DBFType.NUMERIC;
        } else if (String.class.equals(type)) {
            return DBFType.CHARACTER;
        } else if (Date.class.equals(type)) {
            return DBFType.DATE;
        } else if (Boolean.class.equals(type)) {
            return DBFType.LOGICAL;
        }

        else if (boolean.class.equals(type)) {
            return DBFType.LOGICAL;
        }else if (float.class.equals(type)) {
            return DBFType.FLOAT;
        }else if (double.class.equals(type)) {
            return DBFType.NUMERIC;
        }else if (int.class.equals(type)) {
            return DBFType.NUMERIC;
        }
        return DBFType.CHARACTER;
    }

    private static DBFField generateDBFField(String fieldName,DBFType dbfType, Class<?> javaType){
        switch (dbfType){
            case CHARACTER: return new CharacterField(fieldName, (short) 255);
            case DATE: return new DateField(fieldName);
            case FLOAT: return new FloatField(fieldName,(short)50,(short)3);
            case LOGICAL:return new LogicalField(fieldName);
            case NUMERIC: {
                if (javaType == Integer.class || javaType== int.class){
                    return new NumericField(fieldName,(short)50,(short)0);
                }else{
                    return new NumericField(fieldName,(short)50,(short)3);
                }
            }
        }
        return null;
    }

    private static <T> DBFField[] _byGettersNamedStrategy(Class<T> tClass) {
        Method[] getters = removeStaticMethods(removeNotGetterMethods(tClass.getMethods()));
        List<DBFField> res = new ArrayList<>();
        for (Method method:getters){
            res.add(generateFieldByMethod(method));
        }
        return res.toArray(new DBFField[0]);
    }

    private static DBFField generateFieldByMethod(Method method) {
        String fieldName;
        if (method.getName().startsWith("get") || method.getName().startsWith("set")){
            fieldName = method.getName().substring(3,4).toLowerCase()+method.getName().substring(4);
        }else{
            fieldName = method.getName();
        }
        DBFType dbfType = generateDBFType(method.getReturnType());
        if (dbfType == null)
            throw new DBFIllegalFieldException(String.format("Cannot mapping dbf field type %s", method.getReturnType()));
        return generateDBFField(fieldName,dbfType,method.getReturnType());
    }

    private static DBFField[] _getDBFFieldsByAnnotationFields(Field[] fields) {
        List<DBFField> dbfFields = new ArrayList<>();
        for (Field field:fields){
            if (!field.isAnnotationPresent(DBFColumn.class))
                continue;
            DBFColumn annotation =
                    field.getAnnotation(DBFColumn.class);
            dbfFields.add(generateDBFField(annotation));
        }
        return dbfFields.toArray(new DBFField[0]);
    }

    private static DBFField[] _getDBFFieldsByAnnotationGetters(Method[] methods) {
        List<DBFField> dbfFields = new ArrayList<>();
        methods = orderAnnotatedMethods(removeNotGetterMethods(methods));
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class))
                dbfFields.add(generateFieldByAnnotationMethod(method));
        }
        return dbfFields.toArray(new DBFField[0]);
    }

    private static DBFField[] _getDBFFieldsWithoutAnnotation(Method[] methods) {
        List<DBFField> res = new ArrayList<>();
        for (Method method:methods){
            res.add(generateFieldByMethod(method));
        }
        return res.toArray(new DBFField[0]);
    }

    private static DBFField fieldAnnotationMapping(DBFColumn annotation, List<DBFField> fields) {
        for (DBFField field:fields){
            if (field.getName().equals(annotation.name())) return field;
        }
        return null;
    }

    private static byte _getFromDate(int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (dayOfMonth == Calendar.YEAR){
            String yearAsString = String.valueOf(calendar.get(Calendar.YEAR));
            return Byte.parseByte(yearAsString.substring(2));
        }
        int m = calendar.get(dayOfMonth)+1;
        return (byte) m;
    }

    static byte[] getFieldDefinition(DBFField[] fields) {
        byte[] res = new byte[32*fields.length];
        int startPositionToCopy = 0;
        for (DBFField field:fields){
            byte [] fieldDefinitionAsByteArray = getFieldDefinitionAsByteArray(field);
            arraycopy(fieldDefinitionAsByteArray,0,res,startPositionToCopy,fieldDefinitionAsByteArray.length);
            startPositionToCopy += 32;
        }
        return res;
    }

    static byte[] getFieldDefinitionAsByteArray(DBFField field) {
        byte[] res = new byte[32];
        byte[] fieldNameByBytes = new byte[11];
        String fieldName = field.getName();
        if (fieldName.length()<=11){
            arraycopy(fieldName.getBytes(),0,fieldNameByBytes,0,fieldName.getBytes().length);
        }else{
            throw new DBFIllegalFieldException("Size of dbf field's name cannot be more 11 ");
        }
        arraycopy(fieldNameByBytes,0,res,0,fieldNameByBytes.length);
        res[11] = (byte) field.getDbfType().getSymbol();
        res[16] = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) field.getSize()).get(0);
        res[17] = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) field.getDecimalSize()).get(0);
        res[18] = 0x02;
        return res;

    }

    static <T> byte[] toDBFFieldAsByteArray(DBFField dbfField, T row) {
        Object value = getValueFromObject(dbfField,row);
        return _getValueFromObject(dbfField,value);
    }

    static private byte[] _getValueFromObject(DBFField dbfField, Object value) {
        switch (dbfField.getDbfType()) {
            case DATE:
                return dateToByteArray((Date) value);
            case CHARACTER:
                return stringToByteArray((String)value,dbfField);
            case FLOAT: {
                if (value instanceof Double)
                    return floatToByteArray(((Double) value).floatValue(), dbfField);
                if (value instanceof Integer){
                    return floatToByteArray(((Integer) value).floatValue(), dbfField);
                }else return floatToByteArray((Float) value, dbfField);
            }
            case LOGICAL:
                return booleanToByteArray((Boolean) value);
            case NUMERIC: {
                if (value instanceof Double) return doubleToByteArray((Double) value,dbfField);
                else return (integerToByteArray((Integer) value,dbfField));
            }
        }
        return null;
    }

    static byte[] dateToByteArray(Date date) {
        byte[] res = new byte[8];
        if (date == null) return res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DBF_DATE_FORMAT);
        String dateAsString = simpleDateFormat.format(date);
        byte[] dateAsByteArray = dateAsString.getBytes();
        arraycopy(dateAsByteArray, 0, res, 0, dateAsString.getBytes().length);
        return res;
    }

    private static byte[] floatToByteArray(Float asFloat, DBFField field) {
        return stringToByteArray(String.valueOf(asFloat),field);
    }

    private static byte[] booleanToByteArray(Boolean asLogic) {
        if (asLogic == null) asLogic = Boolean.FALSE;
        byte[] res = new byte[1];
        res[0] = (byte) (asLogic ? 84:70);
        return res;
    }

    private static byte[] integerToByteArray(Integer asInteger, DBFField dbfField) {
        return stringToByteArray(String.valueOf(asInteger),dbfField);
    }

    private static byte[] doubleToByteArray(Double asDouble, DBFField dbfField) {
        return stringToByteArray(String.valueOf(asDouble),dbfField);
    }

    private static boolean _checkLogicalFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()){
            case EQUAL: return row.getAsLogic(filteredField) == Boolean.parseBoolean(val);
            case NOT_EQUAL: return row.getAsLogic(filteredField) != Boolean.parseBoolean(val);
        }
        return false;
    }

    private static byte[] stringToByteArray(String value, DBFField f) {
        if (value == null || value.isEmpty()){
            return new byte[f.getSize()];
        }
        if (value.length() > f.getSize()){
            throw new DBFIllegalValueException(String.format("Cannot set string value %s to field %s. Size value = %s. Size field = %s",value,f.getName(),value.length(),f.getSize()));
        }
        if (value.length() == f.getSize()) return value.getBytes();
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        for (int i=value.length();i<f.getSize();i++){
            sb.append(' ');
        }
        return sb.toString().getBytes();
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

    private static byte[] toDBFFieldAsByteArray(DBFField dbfField, Object[] row, int i) {
        Object value = row[i];
        return _getValueFromObject(dbfField,value);
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
                if (setterMethod != null) invoke(setterMethod, t, val);
                else throw new RuntimeException("Getter not found");
            }
        }
    }

    private static <T> Object getValueFromObject(DBFField dbfField, T row) {
        DBFGenerateStrategies strategies;
        DBFTable dbfTable = row.getClass().getAnnotation(DBFTable.class);
        if (dbfTable == null) strategies = DBFGenerateStrategies.AUTOMATIC;
        else
            strategies = dbfTable.accessMode();
        switch (strategies){
            case AUTOMATIC:  return _getValueByAutomaticValue(dbfField,row);
            case ANNOTATED_FIELDS: return _getValueByAnnotatedField(dbfField,row);
            case FIELDS_NAME: return _getValueByFieldName(dbfField,row);
            case GETTERS_NAME: return _getValueByGettersName(dbfField,row);
            case ANNOTATED_GETTERS: return _getValueByAnnotatedGetters(dbfField,row);
        }
        return null;
    }

    private static <T> Object _getValueByGettersName(DBFField dbfField, T row) {
        Method[] methods = removeNotGetterMethods(removeStaticMethods(row.getClass().getDeclaredMethods()));
        for (Method method:methods){
            if (method.getName().substring(3)
                    .equalsIgnoreCase(dbfField.getName())) return invoke(method,row);
        }
        return null;
    }


    private static <T> Object _getValueByAnnotatedGetters(DBFField dbfField, T row) {
        Method[] methods = removeStaticMethods(row.getClass().getDeclaredMethods());
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class) && method.getAnnotation(DBFColumn.class).name().equals(dbfField.getName()))
                return invoke(method,row);
        }

        return null;
    }

    private static <T> Object _getValueByFieldName(DBFField dbfField, T row) {
        Field[] fields = removeStaticField(row.getClass().getDeclaredFields());
        for (Field field:fields){
            if (field.getName().equalsIgnoreCase(dbfField.getName())) return getValueFromField(field,row);
        }
        return null;
    }
    private static <T> Object _getValueByAnnotatedField(DBFField dbfField, T row) {
        Field[] fields = removeStaticField(row.getClass().getDeclaredFields());
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class) &&
                    field.getAnnotation(DBFColumn.class).name().equals(dbfField.getName())){
                return getValueFromField(field,row);
            }
        }
        return null;
    }

    private static <T> Object _getValueByAutomaticValue(DBFField dbfField, T row) {
        Field[] fields = removeStaticField(row.getClass().getDeclaredFields());
        Method[] methods = removeStaticMethods(row.getClass().getDeclaredMethods());
        boolean isOneGetterMethodContainsAnnotation = checkGettersOnAnnotation(methods);
        boolean isOneFieldContainsAnnotation = checkFieldsOnAnnotation(fields);

        if (isOneFieldContainsAnnotation && isOneGetterMethodContainsAnnotation){
            for (Method method:methods){
                if (method.isAnnotationPresent(DBFColumn.class) && method.getAnnotation(DBFColumn.class).name().equals(dbfField.getName()))
                    return invoke(method,row);
            }
            for (Field field:fields){
                if (field.isAnnotationPresent(DBFColumn.class) && field.getAnnotation(DBFColumn.class).name().equals(dbfField.getName())){
                    return getValueFromField(field,row);
                }
            }
            return null;
        }

        if (!isOneGetterMethodContainsAnnotation && isOneFieldContainsAnnotation){
            for (Field field:fields){
                if (field.isAnnotationPresent(DBFColumn.class) && field.getAnnotation(DBFColumn.class).name().equals(dbfField.getName())){
                    return getValueFromField(field,row);
                }
            }
            return null;
        }
        if (isOneGetterMethodContainsAnnotation){
            for (Method method:methods){
                if (method.isAnnotationPresent(DBFColumn.class) && method.getAnnotation(DBFColumn.class).name().equalsIgnoreCase(dbfField.getName())){
                    return invoke(method,row);
                }
            }
            return null;
        }

        for (Method method:methods){
            if (method.getName()
                    .equalsIgnoreCase(dbfField.getName().substring(3))) return invoke(method,row);
        }
        for (Field field:fields){
            if (field.getName().equalsIgnoreCase(dbfField.getName())) return getValueFromField(field,row);
        }
        return null;
    }

    private static short calculateRecordSize(DBFField[] fields) {
        short res = 0;
        for (DBFField dbfField:fields){
            res +=dbfField.getSize();
        }
        res++;
        return res;
    }

    private static Method[] removeStaticMethods(Method[] declaredMethods) {
        int fieldCount = 0;
        for (Method method:declaredMethods){
            if (!isStatic(method.getModifiers())) fieldCount++;
        }
        Method[] res = new Method[fieldCount];
        int i=0;
        for (Method method:declaredMethods){
            if (!isStatic(method.getModifiers())) res[i++]=method;
        }
        return res;
    }

    private static Field[] removeStaticField(Field[] declaredFields) {
        int fieldCount = 0;
        for (Field f:declaredFields){
            if (!isStatic(f.getModifiers())) fieldCount++;
        }
        Field[] res = new Field[fieldCount];
        int i=0;
        for (Field f:declaredFields){
            if (!isStatic(f.getModifiers())) res[i++]=f;
        }
        return res;
    }

    private static boolean checkGettersOnAnnotation(Method[] methods) {
        if (methods == null) return false;
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class))
                return true;
        }
        return false;
    }

    private static boolean checkFieldsOnAnnotation(Field[] fields) {
        if (fields == null) return false;
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class))
                return true;
        }
        return false;
    }

    private static Object invoke(Method method,Object row){
        try {
            return method.invoke(row);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void invoke(Method method, Object row, Object ... parameters){
        try {
            method.invoke(row, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static  Method[] removeNotGetterMethods(Method[] methods) {
        List<Method> resMethods = new ArrayList<>();
        for (Method method:methods){
            if (method.getName().startsWith("get") && !method.getName().equalsIgnoreCase("getClass")) {
                resMethods.add(method);
            }
        }
        return resMethods.toArray(new Method[0]);
    }

    private static Field[] getAnnotatedFields(Field[] fields) {
        List<Field> res = new ArrayList<>();
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class)){
                res.add(field);
            }
        }
        return res.toArray(new Field[0]);
    }

    private static Method[] orderAnnotatedMethods(Method[] getters) {
        Map<Integer,Method> orderedMap = new TreeMap<>();
        for (Method method:getters) {
            int position = 0;
            DBFColumn column = method.getAnnotation(DBFColumn.class);
            if (column != null){
                position = column.positionOnDBFFile();
            }
            orderedMap.put(position,method);
        }
        return orderedMap.values().toArray(new Method[0]);
    }

    private static Field[] orderAnnotatedFields(Field[] fields){
        Map<Integer,Field> orderedMap = new TreeMap<>();
        for (Field field:fields) {
            int position = 0;
            DBFColumn column = field.getAnnotation(DBFColumn.class);
            if (column != null){
                position = column.positionOnDBFFile();
            }
            orderedMap.put(position,field);
        }
        return orderedMap.values().toArray(new Field[0]);
    }

    private static Method[] getAnnotatedMethods(Method[] methods) {
        List<Method> res = new ArrayList<>();
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class)){
                res.add(method);
            }
        }
        return res.toArray(new Method[0]);
    }

    private static boolean hasAnnotatedGetter(Field field, Method[] methods) {
        String fieldName = ("get"+field.getName()).toLowerCase();
        for (Method method:methods){
            if (method.getName().equalsIgnoreCase(fieldName)) return true;
        }
        return false;
    }

    private static Object getValueFromField(Field field,Object t){
        try {
            field.setAccessible(true);
            return field.get(t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
