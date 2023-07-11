package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public class CharacterField extends DBFField {
    public CharacterField(String name, int size) {
        super(name, DBFType.CHARACTER, size, (short) 0);
    }
}
