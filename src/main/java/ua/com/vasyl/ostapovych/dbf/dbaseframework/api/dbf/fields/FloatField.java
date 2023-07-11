package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public class FloatField extends DBFField{

    public FloatField(String name, int size, int decimalSize) {
        super(name, DBFType.FLOAT, size, decimalSize);
    }
}
