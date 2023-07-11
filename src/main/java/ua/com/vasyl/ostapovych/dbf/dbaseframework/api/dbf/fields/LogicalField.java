package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public class LogicalField extends DBFField{
    public LogicalField(String name) {
        super(name, DBFType.LOGICAL, 1, 0);
    }
}
