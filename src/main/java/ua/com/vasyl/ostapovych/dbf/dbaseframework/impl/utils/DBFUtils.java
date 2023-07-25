package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies.AUTOMATIC;

public class DBFUtils {

    public static final String DBF_DATE_FORMAT = "yyyyMMdd";

    public static Field[] removeStaticField(Field[] declaredFields) {
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

    public static  Method[] removeNotGetterMethods(Method[] methods) {
        List<Method> resMethods = new ArrayList<>();
        for (Method method:methods){
            if (method.getName().startsWith("get") && !method.getName().equalsIgnoreCase("getClass")) {
                resMethods.add(method);
            }
        }
        return resMethods.toArray(new Method[0]);
    }

    public static Method[] removeStaticMethods(Method[] declaredMethods) {
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

    public static boolean checkGettersOnAnnotation(Method[] methods) {
        if (methods == null) return false;
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class))
                return true;
        }
        return false;
    }

    public static boolean checkFieldsOnAnnotation(Field[] fields) {
        if (fields == null) return false;
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class))
                return true;
        }
        return false;
    }

    public static DBFField generateDBFField(String fieldName, DBFType type, short size, short decimalCount) {
        switch (type){
            case LOGICAL: return new LogicalField(fieldName);
            case DATE: return new DateField(fieldName);
            case CHARACTER: return new CharacterField(fieldName,size);
            case NUMERIC: return new NumericField(fieldName,size,decimalCount);
            case FLOAT: return new FloatField(fieldName,size,decimalCount);
        }
        return new UnknownField(fieldName);
    }

    public static DBFField generateDBFField(DBFColumn annotation){
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

    public static void invoke(Method method, Object row, Object ... parameters){
        try {
            method.invoke(row, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static DBFGenerateStrategies checkStrategy(Class<?> tClass) {
        if (!tClass.isAnnotationPresent(DBFTable.class)) return AUTOMATIC;
        DBFTable table = tClass.getAnnotation(DBFTable.class);
        return table.accessMode();
    }


}
