package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.validators;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.validation.DBFDuplicateAnnotationFieldName;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Validators {
    public static <T> void validateMetaDataDBF(Class<T> type) {
        validateForDuplicateCaptionName(type);
    }

    public static <T> void validateForDuplicateCaptionName(Class<T> type) {
        if (type == null) {
            return;
        }
        Set<String> names = new HashSet<>();
        Field[] fields = type.getDeclaredFields();
        for (Field f:fields){
           DBFColumn annotation = f.getAnnotation(DBFColumn.class);
           if (annotation == null){
               continue;
           }
           if (names.contains(annotation.name())){
               throw new DBFDuplicateAnnotationFieldName(
                       String.format("Name %s is more than one time declared",annotation.name()));
           }else{
               names.add(annotation.name());
           }
        }
    }
}
