package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.reflect.Modifier.isStatic;


public class __ReflectionUtils__ {
    Object getValueFromField(Field field,Object t){
        try {
            field.setAccessible(true);
            return field.get(t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Method[] getAnnotatedMethods(Method[] methods) {
        List<Method> res = new ArrayList<Method>();
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class)){
                res.add(method);
            }
        }
        return res.toArray(new Method[0]);
    }

    Field[] getAnnotatedFields(Field[] fields) {
        List<Field> res = new ArrayList<Field>();
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class)){
                res.add(field);
            }
        }
        return res.toArray(new Field[0]);
    }

    Object invoke(Method method,Object row){
        try {
            return method.invoke(row);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    void invoke(Method method, Object row, Object ... parameters){
        try {
            method.invoke(row, parameters);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    Method[] orderAnnotatedMethods(Method[] getters) {
        Map<Integer,Method> orderedMap = new TreeMap<Integer, Method>();
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

    Field[] orderAnnotatedFields(Field[] fields){
        Map<Integer,Field> orderedMap = new TreeMap<Integer, Field>();
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

    boolean hasAnnotatedGetter(Field field, Method[] methods) {
        String fieldName = ("get"+field.getName()).toLowerCase();
        for (Method method:methods){
            if (method.getName().equalsIgnoreCase(fieldName)) return true;
        }
        return false;
    }
    Method[] removeNotGetterMethods(Method[] methods) {
        List<Method> resMethods = new ArrayList<Method>();
        for (Method method:methods){
            if (method.getName().startsWith("get") && !method.getName().equalsIgnoreCase("getClass")) {
                resMethods.add(method);
            }
        }
        return resMethods.toArray(new Method[0]);
    }
    boolean checkFieldsOnAnnotation(Field[] fields) {
        if (fields == null) return false;
        for (Field field:fields){
            if (field.isAnnotationPresent(DBFColumn.class))
                return true;
        }
        return false;
    }

    boolean checkGettersOnAnnotation(Method[] methods) {
        if (methods == null) return false;
        for (Method method:methods){
            if (method.isAnnotationPresent(DBFColumn.class))
                return true;
        }
        return false;
    }

    Method[] removeStaticMethods(Method[] declaredMethods) {
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

    Field[] removeStaticField(Field[] declaredFields) {
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

    public <T> T createInstance(Class<T> type) {
        try {
             return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Method getSetterMethod(Method method,T t) {
        String methodName = method.getName();
        String setterMethodName = methodName.replace("get","set");
        Class<?> tClass = t.getClass();
        for (Method m:tClass.getDeclaredMethods()){
            if (m.getName().equals(setterMethodName)) return m;
        }
        return null;
    }

    public <T> void setValueToField(Field field, T t,Object val) {
        field.setAccessible(true);
        try {
            field.set(t,val);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> boolean isMethodSetter(Method method,T t) {
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
}
