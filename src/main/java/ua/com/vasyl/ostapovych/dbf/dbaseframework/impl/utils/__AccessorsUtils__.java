package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


class __AccessorsUtils__ {
    private final __ReflectionUtils__ reflectionUtils = new __ReflectionUtils__();

    <T> Object _getValueByGettersName(DBFField dbfField, T row) {
        Method[] methods = reflectionUtils.removeNotGetterMethods(reflectionUtils.removeStaticMethods(row.getClass().getDeclaredMethods()));
        for (Method method:methods){
            if (method.getName().substring(3)
                    .equalsIgnoreCase(dbfField.getName())) return reflectionUtils.invoke(method,row);
        }
        return null;
    }

    <T> Object _getValueByFieldName(DBFField dbfField, T row) {
        Field[] fields = reflectionUtils.removeStaticField(row.getClass().getDeclaredFields());
        for (Field field:fields){
            if (field.getName().equalsIgnoreCase(dbfField.getName())) return reflectionUtils.getValueFromField(field,row);
        }
        return null;
    }

    <T> Object _getValueByAnnotatedGetters(DBFField dbfField, T row) {
        Method[] methods = reflectionUtils.removeStaticMethods(row.getClass().getDeclaredMethods());
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class) && method.getAnnotation(DBFColumn.class).name().equals(dbfField.getName()))
                return reflectionUtils.invoke(method,row);
        }

        return null;
    }

    <T> Object _getValueByAnnotatedField(DBFField dbfField, T row) {
        Field[] fields = reflectionUtils.removeStaticField(row.getClass().getDeclaredFields());
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class) &&
                    field.getAnnotation(DBFColumn.class).name().equals(dbfField.getName())){
                return reflectionUtils.getValueFromField(field,row);
            }
        }
        return null;
    }

    <T> Object _getValueByAutomaticValue(DBFField dbfField, T row) {
        Field[] fields = reflectionUtils.removeStaticField(row.getClass().getDeclaredFields());
        Method[] methods = reflectionUtils.removeStaticMethods(row.getClass().getDeclaredMethods());
        boolean isOneGetterMethodContainsAnnotation = reflectionUtils.checkGettersOnAnnotation(methods);
        boolean isOneFieldContainsAnnotation = reflectionUtils.checkFieldsOnAnnotation(fields);

        if (isOneFieldContainsAnnotation && isOneGetterMethodContainsAnnotation){
            for (Method method:methods){
                if (method.isAnnotationPresent(DBFColumn.class) && method.getAnnotation(DBFColumn.class).name().equals(dbfField.getName()))
                    return reflectionUtils.invoke(method,row);
            }
            for (Field field:fields){
                if (field.isAnnotationPresent(DBFColumn.class) && field.getAnnotation(DBFColumn.class).name().equals(dbfField.getName())){
                    return reflectionUtils.getValueFromField(field,row);
                }
            }
            return null;
        }

        if (!isOneGetterMethodContainsAnnotation && isOneFieldContainsAnnotation){
            for (Field field:fields){
                if (field.isAnnotationPresent(DBFColumn.class) && field.getAnnotation(DBFColumn.class).name().equals(dbfField.getName())){
                    return reflectionUtils.getValueFromField(field,row);
                }
            }
            return null;
        }
        if (isOneGetterMethodContainsAnnotation){
            for (Method method:methods){
                if (method.isAnnotationPresent(DBFColumn.class) && method.getAnnotation(DBFColumn.class).name().equalsIgnoreCase(dbfField.getName())){
                    return reflectionUtils.invoke(method,row);
                }
            }
            return null;
        }

        for (Method method:methods){
            if (method.getName()
                    .equalsIgnoreCase(dbfField.getName().substring(3))) return reflectionUtils.invoke(method,row);
        }
        for (Field field:fields){
            if (field.getName().equalsIgnoreCase(dbfField.getName())) return reflectionUtils.getValueFromField(field,row);
        }
        return null;
    }

    <T> Object getValueFromObject(DBFField dbfField, T row) {
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

    public <T> T fillByAutomaticStrategy(T t, DBFRow row) {
        if (t == null || row == null) return null;
        Field[] fields = reflectionUtils.removeStaticField(t.getClass().getDeclaredFields());
        Method[] methods = reflectionUtils.removeStaticMethods(t.getClass().getDeclaredMethods());
        boolean isOneGetterMethodContainsAnnotation = reflectionUtils.checkGettersOnAnnotation(methods);
        boolean isOneFieldContainsAnnotation = reflectionUtils.checkFieldsOnAnnotation(fields);

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
            boolean methodIsSetter = reflectionUtils.isMethodSetter(method,t);
            if (methodIsSetter){
                String fieldName = method.getName().substring(3,4).toLowerCase()+method.getName().substring(4);
                if (row.isContainField(fieldName)){
                    Object val = row.getAsObject(fieldName);
                    reflectionUtils.invoke(method,t,val);
                }

            }
        }
        return t;
    }

    public <T> T fillByAnnotatedFieldStrategy(T t, DBFRow row) {
        Field[] fields = reflectionUtils.removeStaticField(t.getClass().getDeclaredFields());
        _fillByFieldMethod(fields,row,t);
        return t;
    }

    public <T> T fillByAnnotatedGetterFieldStrategy(T t, DBFRow row) {
        Method[] methods = reflectionUtils.removeStaticMethods(t.getClass().getDeclaredMethods());
        _fillByGettersMethod(methods,row,t);
        return t;
    }

    public <T> T fillByFieldNameStrategy(T t, DBFRow row) {
        Field [] fields = t.getClass().getDeclaredFields();
        for (Field field:fields){
            if (row.isContainField(field.getName())){
                reflectionUtils.setValueToField(field,t,row.getAsObject(field.getName()));
            }
        }
        return t;
    }

    public <T> T fillByGetterNameStrategy(T t, DBFRow row) {
        Method [] methods = reflectionUtils.removeStaticMethods(t.getClass().getDeclaredMethods());
        for (Method method:methods){
            if (reflectionUtils.isMethodSetter(method,t)) {
                String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                if (row.isContainField(fieldName)) {
                    reflectionUtils.invoke(method, t, row.getAsObject(fieldName));
                }
            }
        }
        return t;
    }

    private <T> void _fillByGettersMethod(Method[] methods,DBFRow row, T t){
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class)) {
                DBFColumn annotation = method.getAnnotation(DBFColumn.class);
                Object val = row.getAsObject(annotation.name());
                Method setterMethod = reflectionUtils.getSetterMethod(method,t);
                reflectionUtils.invoke(setterMethod,t,val);
            }
        }
    }

    private <T> void _fillByFieldMethod(Field [] fields,DBFRow row, T t){
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class)){
                DBFColumn annotation = field.getAnnotation(DBFColumn.class);
                Object val = row.getAsObject(annotation.name());
                reflectionUtils.setValueToField(field,t,val);
            }
        }
    }



}
