package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalFieldException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Character.isDigit;
import static java.lang.System.*;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies.AUTOMATIC;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils.*;


final class WriteUtils {

    public static  <T> DBFField[] generateFields(Class<T> tClass, DBFGenerateStrategies strategy) {
        switch (strategy){
            case AUTOMATIC: return automaticStrategy(tClass);
            case ANNOTATED_FIELDS: return annotatedFieldsStrategy(tClass);
            case ANNOTATED_GETTERS: return annotatedGetterStrategy(tClass);
            case FIELDS_NAME: return byFieldNameStrategy(tClass);
            case GETTERS_NAME: return byGettersNamedStrategy(tClass);
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

    private static <T> DBFField[] automaticStrategy(Class<T> tClass) {
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

    private static <T> DBFField[] annotatedFieldsStrategy(Class<T> tClass) {
        Field[] fields = getAnnotatedFields(removeStaticField(tClass.getDeclaredFields()));
        List<DBFField> res = new ArrayList<>();
        for (Field field:fields){
            res.add(DBFUtils.generateDBFField(field.getAnnotation(DBFColumn.class)));
        }
        return res.toArray(new DBFField[0]);
    }

    private static <T> DBFField[] annotatedGetterStrategy(Class<T> tClass) {
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

    private static <T> DBFField[] byFieldNameStrategy(Class<T> tClass) {
        Field[] fields = removeStaticField(tClass.getDeclaredFields());
        List<DBFField> res = new ArrayList<>();
        for (Field field:fields){
            res.add(generateDBFFieldFromClassField(field));
        }
        return res.toArray(new DBFField[0]);
    }

    private static <T> DBFField[] byGettersNamedStrategy(Class<T> tClass) {
        Method[] getters = removeStaticMethods(removeNotGetterMethods(tClass.getMethods()));
        List<DBFField> res = new ArrayList<>();
        for (Method method:getters){
            res.add(generateFieldByMethod(method));
        }
        return res.toArray(new DBFField[0]);
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

    private static DBFField[] _getDBFFieldsWithoutAnnotation(Method[] methods) {
        List<DBFField> res = new ArrayList<>();
        for (Method method:methods){
            res.add(generateFieldByMethod(method));
        }
        return res.toArray(new DBFField[0]);
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
                res.add(DBFUtils.generateDBFField(field.getAnnotation(DBFColumn.class)));
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
            dbfFields.add(DBFUtils.generateDBFField(annotation));
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



    private static DBFField fieldAnnotationMapping(DBFColumn annotation, List<DBFField> fields) {
        for (DBFField field:fields){
            if (field.getName().equals(annotation.name())) return field;
        }
        return null;
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

    private static byte[] toDBFFieldAsByteArray(DBFField dbfField, Object[] row, int i) {
        Object value = row[i];
        return _getValueFromObject(dbfField,value);
    }

    private static short calculateRecordSize(DBFField[] fields) {
        short res = 0;
        for (DBFField dbfField:fields){
            res +=dbfField.getSize();
        }
        res++;
        return res;
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

    private static DBFField generateFieldByAnnotationMethod(Method method) {
        DBFColumn annotation =
                method.getAnnotation(DBFColumn.class);
        return DBFUtils.generateDBFField(annotation);
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
                if (dbfField.getDecimalSize() >0) {
                    return doubleToByteArray((Double) value, dbfField);
                }
                else{
                    return (integerToByteArray((Integer) value,dbfField));
                }
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

    private static byte[] floatToByteArray(Float value, DBFField field) {
        String floatAsString = String.valueOf(value);
        int allCountDigits = getCountIntPartDigit(floatAsString);
        if (allCountDigits >field.getSize()){
            throw new DBFIllegalValueException(
                    String.format("Cannot set float value %s to field %s. Size value = %s. Size field = %s",
                            value,field.getName(),allCountDigits,field.getSize()));
        }
        if (field.getSize() == 1 && value <0){
            Logger.getLogger(WriteUtils.class.getSimpleName())
                    .log(Level.WARNING,
                            String.format("Cannot insert negative value %s in float type field %s with size %d. 0.0 will be written",
                                    value,field.getName(),field.getSize()));
            return new byte[]{0};
        }
        return stringToByteArray(String.valueOf(value),field);
    }

    private static int getCountIntPartDigit(String floatAsString) {
        if (floatAsString == null){
            return -1;
        }
        char[] charArray = floatAsString.toCharArray();
        int countWholePart = 0;
        int countFractionPart = 0;

        boolean isWhole = true;
        for (char c : charArray) {
            if (Character.isLetter(c)) {
                return -1;
            }
            if (c == '.') {
                isWhole = false;
                continue;
            }

            if (!isDigit(c)){
                continue;
            }

            if (isWhole) {
                countWholePart++;
            } else {
                countFractionPart++;
            }
        }
        int indexDecimalDelimiter = floatAsString.indexOf('.');
        if (indexDecimalDelimiter  == -1) {
            return countWholePart;
        }else {
            int valFractionPart = Integer.parseInt(floatAsString.substring(indexDecimalDelimiter+1));
            if (valFractionPart == 0){
                return countWholePart;
            }else{
                return countWholePart+countFractionPart;
            }
        }

    }

    private static byte[] booleanToByteArray(Boolean asLogic) {
        if (asLogic == null) asLogic = Boolean.FALSE;
        byte[] res = new byte[1];
        res[0] = (byte) (asLogic ? 84:70);
        return res;
    }

    private static byte[] integerToByteArray(Integer value, DBFField field) {
        return numberStringToByteArray(value,field);
    }

    private static byte[] numberStringToByteArray(Number value, DBFField field) {
        String floatAsString = String.valueOf(value);
        int allCountDigits = getCountIntPartDigit(floatAsString);
        if (allCountDigits >field.getSize()){
            throw new DBFIllegalValueException(
                    String.format("Cannot set float value %s to field %s. Size value = %s. Size field = %s",
                            value,field.getName(),allCountDigits,field.getSize()));
        }
        if (value instanceof Integer) {
            if (field.getSize() == 1 && value.intValue() < 0) {
                Logger.getLogger(WriteUtils.class.getSimpleName())
                        .log(Level.WARNING,
                                String.format("Cannot insert negative value %s in float type field %s with size %d. 0.0 will be written",
                                        value, field.getName(), field.getSize()));
                return new byte[]{0};
            }
        }
        if (value instanceof Double){
            if (field.getSize() == 1 && value.doubleValue() <0){
                Logger.getLogger(WriteUtils.class.getSimpleName())
                        .log(Level.WARNING,
                                String.format("Cannot insert negative value %s in float type field %s with size %d. 0.0 will be written",
                                        value,field.getName(),field.getSize()));
                return new byte[]{0};
            }
        }
        return stringToByteArray(String.valueOf(value),field);
    }

    private static byte[] doubleToByteArray(Double value, DBFField field) {
        return numberStringToByteArray(value,field);
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

    private static <T> Object getValueFromObject(DBFField dbfField, T row) {
        DBFGenerateStrategies strategies;
        DBFTable dbfTable = row.getClass().getAnnotation(DBFTable.class);
        if (dbfTable == null) strategies = AUTOMATIC;
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

    private static Object getValueFromField(Field field,Object t){
        try {
            field.setAccessible(true);
            return field.get(t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object invoke(Method method,Object row){
        try {
            return method.invoke(row);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }




}
