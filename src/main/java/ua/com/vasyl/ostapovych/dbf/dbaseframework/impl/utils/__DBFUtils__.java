package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalFieldException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class __DBFUtils__ {

    private final __ReflectionUtils__ reflectionUtils = new __ReflectionUtils__();

    DBFType recognizeType(char rawType) {
        switch (rawType){
            case 'C' : return DBFType.CHARACTER;
            case 'M' : return DBFType.MEMO;
            case 'N' : return DBFType.NUMERIC;
            case 'D' : return DBFType.DATE;
            case 'F' : return DBFType.FLOAT;
            case 'L' : return DBFType.LOGICAL;
        }
        return DBFType.UNKNOWN;
    }

    DBFField generateDBFField(String fieldName, DBFType type, short size, short decimalCount) {
        switch (type){
            case LOGICAL: return new LogicalField(fieldName);
            case DATE: return new DateField(fieldName);
            case CHARACTER: return new CharacterField(fieldName,size);
            case NUMERIC: return new NumericField(fieldName,size,decimalCount);
            case FLOAT: return new FloatField(fieldName,size,decimalCount);
        }
        return new UnknownField(fieldName);
    }


    <T> DBFField[] generateFields(Class<T> tClass, DBFGenerateStrategies strategy) {

        switch (strategy){
            case AUTOMATIC: return _automaticStrategy(tClass);
            case ANNOTATED_FIELDS: return _annotatedFieldsStrategy(tClass);
            case ANNOTATED_GETTERS: return _annotatedGetterStrategy(tClass);
            case FIELDS_NAME: return _byFieldNameStrategy(tClass);
            case GETTERS_NAME: return _byGettersNamedStrategy(tClass);
        }
        return new DBFField[0];
    }

    <T> DBFField[] _byGettersNamedStrategy(Class<T> tClass) {
        Method[] getters = reflectionUtils.removeStaticMethods(reflectionUtils.removeNotGetterMethods(tClass.getMethods()));
        List<DBFField> res = new ArrayList<DBFField>();
        for (Method method:getters){
            res.add(generateFieldByMethod(method));
        }
        return res.toArray(new DBFField[0]);
    }

    <T> DBFField[] _byFieldNameStrategy(Class<T> tClass) {
        Field[] fields = reflectionUtils.removeStaticField(tClass.getDeclaredFields());
        List<DBFField> res = new ArrayList<DBFField>();
        for (Field field:fields){
            res.add(generateDBFFieldFromClassField(field));
        }
        return res.toArray(new DBFField[0]);
    }

    <T> DBFField[] _annotatedGetterStrategy(Class<T> tClass) {
        Method [] getters = reflectionUtils.getAnnotatedMethods(
                reflectionUtils.removeStaticMethods(
                        reflectionUtils.removeNotGetterMethods(tClass.getMethods())));
        getters = reflectionUtils.orderAnnotatedMethods(getters);
        List<DBFField> res = new ArrayList<DBFField>();
        for (Method method:getters){
            res.add(generateFieldByAnnotationMethod(method));
        }
        return res.toArray(new DBFField[0]);
    }

     <T> DBFField[] _annotatedFieldsStrategy(Class<T> tClass) {
        Field[] fields = reflectionUtils.getAnnotatedFields(reflectionUtils.removeStaticField(tClass.getDeclaredFields()));
        List<DBFField> res = new ArrayList<DBFField>();
        for (Field field:fields){
            res.add(generateDBFField(field.getAnnotation(DBFColumn.class)));
        }
        return res.toArray(new DBFField[0]);
    }

     <T> DBFField[] _automaticStrategy(Class<T> tClass) {
        Field[] fields = reflectionUtils.removeStaticField(tClass.getDeclaredFields());
        Method[] methods = reflectionUtils.removeNotGetterMethods(reflectionUtils.removeStaticMethods(tClass.getDeclaredMethods()));
        boolean isOneGetterMethodContainsAnnotation = reflectionUtils.checkGettersOnAnnotation(methods);
        boolean isOneFieldContainsAnnotation = reflectionUtils.checkFieldsOnAnnotation(fields);

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

     DBFField[] _getDBFFieldsWithoutAnnotation(Method[] methods) {
        List<DBFField> res = new ArrayList<DBFField>();
        for (Method method:methods){
            res.add(generateFieldByMethod(method));
        }
        return res.toArray(new DBFField[0]);
    }

     DBFField generateFieldByMethod(Method method) {
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

     DBFField[] _getDBFFieldsAndGettersWithAnnotation(Field[] fields, Method[] methods) {
        List<DBFField> res = new ArrayList<DBFField>();
        methods = reflectionUtils.removeNotGetterMethods(methods);
        Method [] annotatedMethods = reflectionUtils.orderAnnotatedMethods(reflectionUtils.getAnnotatedMethods(methods));
        Field [] annotatedFields = reflectionUtils.orderAnnotatedFields(reflectionUtils.getAnnotatedFields(fields));
        for (Method method:annotatedMethods){
            res.add(generateFieldByAnnotationMethod(method));
        }
        for (Field field:annotatedFields){
            if (!reflectionUtils.hasAnnotatedGetter(field,annotatedMethods)){
                res.add(generateDBFField(field.getAnnotation(DBFColumn.class)));
            }
        }
        Map<Short,DBFField> orderedMap = new TreeMap<Short, DBFField>();
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

    DBFField fieldAnnotationMapping(DBFColumn annotation, List<DBFField> fields) {
        for (DBFField field:fields){
            if (field.getName().equals(annotation.name())) return field;
        }
        return null;
    }


    DBFField[] _getDBFFieldsByAnnotationGetters(Method[] methods) {
        List<DBFField> dbfFields = new ArrayList<DBFField>();
        methods = reflectionUtils.orderAnnotatedMethods(reflectionUtils.removeNotGetterMethods(methods));
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class))
                dbfFields.add(generateFieldByAnnotationMethod(method));
        }
        return dbfFields.toArray(new DBFField[0]);
    }

    DBFField generateFieldByAnnotationMethod(Method method) {
        DBFColumn annotation =
                method.getAnnotation(DBFColumn.class);
        return generateDBFField(annotation);
    }


    DBFField[] _getDBFFieldsByAnnotationFields(Field[] fields) {
        List<DBFField> dbfFields = new ArrayList<DBFField>();
        for (Field field:fields){
            if (!field.isAnnotationPresent(DBFColumn.class))
                continue;
            DBFColumn annotation =
                    field.getAnnotation(DBFColumn.class);
            dbfFields.add(generateDBFField(annotation));
        }
        return dbfFields.toArray(new DBFField[0]);
    }

    DBFField generateDBFFieldFromClassField(Field field) {
        String fileName = field.getName();
        DBFType dbfType = generateDBFType(field.getType());
        if (dbfType == null)
            throw new DBFIllegalFieldException(String.format("Cannot mapping dbf field type %s", field.getType()));
        return generateDBFField(fileName,dbfType,field.getType());
    }

    <T> DBFType generateDBFType(Class<T> type) {
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

    DBFField generateDBFField(String fieldName,DBFType dbfType, Class<?> javaType){
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

    DBFField generateDBFField(DBFColumn annotation){
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

    short calculateRecordSize(DBFField[] fields) {
        short res = 0;
        for (DBFField dbfField:fields){
            res +=dbfField.getSize();
        }
        res++;
        return res;
    }


}
