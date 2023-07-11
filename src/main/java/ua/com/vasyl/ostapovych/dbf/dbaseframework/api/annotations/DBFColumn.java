package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBFColumn {
    String name();
    DBFType type();
    short size() default 0;
    short decimalSize() default 0;
    short positionOnDBFFile() default 0;
}
