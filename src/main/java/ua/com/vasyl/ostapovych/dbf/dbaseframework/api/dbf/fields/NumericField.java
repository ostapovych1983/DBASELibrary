package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public class NumericField extends DBFField {

    public NumericField(String name,  int size, int decimalSize) {
        super(name, DBFType.NUMERIC, size, decimalSize);
    }
}
