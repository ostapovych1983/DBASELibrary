package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public class UnknownField extends DBFField{
    public UnknownField(String name) {
        super(name, DBFType.UNKNOWN, (short)0, (short)0);
    }
}
