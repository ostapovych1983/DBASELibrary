package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies.AUTOMATIC;

@SuppressWarnings("unused")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBFTable {
    String name() default "";
    DBFGenerateStrategies accessMode() default AUTOMATIC;
}
