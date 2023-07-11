package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public class DateField extends DBFField {

    public DateField(String name) {
        super(name, DBFType.DATE, 8, 0);
    }
}
