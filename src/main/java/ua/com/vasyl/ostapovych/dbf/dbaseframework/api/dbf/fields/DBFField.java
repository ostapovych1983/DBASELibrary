package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields;


import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalFieldException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

public abstract class DBFField{
    private final String name;
    private final DBFType dbfType;
    private final int size;
    private final int decimalSize;


    public DBFField(String name, DBFType dbfType, int size, int decimalSize) {
        this.name = name;
        this.dbfType = dbfType;
        if (size > 255) throw new DBFIllegalFieldException("Size of field cannot be more than 255.");
        this.size = size;
        this.decimalSize = decimalSize;
    }

    public String getName() {
        return name;
    }

    public DBFType getDbfType() {
        return dbfType;
    }

    public int getSize() {
        return size;
    }

    public int getDecimalSize() {
        return decimalSize;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBFField field = (DBFField) o;
        return name.equalsIgnoreCase(field.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
