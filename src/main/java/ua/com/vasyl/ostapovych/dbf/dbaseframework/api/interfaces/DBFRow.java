package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.util.Date;
import java.util.Set;

@SuppressWarnings("ALL")
public interface DBFRow {

    Date getAsDate(DBFField field);
    Float getAsFloat(DBFField field);
    String getAsString(DBFField field);
    Boolean getAsLogic(DBFField field);
    Double getAsDouble(DBFField field);
    Integer getAsInteger(DBFField field);

    Date getAsDate(String fieldName);
    Float getAsFloat(String fieldName);
    String getAsString(String fieldName);
    Boolean getAsLogic(String fieldName);
    Double getAsDouble(String fieldName);
    Integer getAsInteger(String fieldName);

    Date getAsDate(int index);
    Float getAsFloat(int index);
    String getAsString(int index);
    Boolean getAsLogic(int index);
    Double getAsDouble(int index);
    Integer getAsInteger(int index);
    boolean isDeleted();

    Set<DBFField> fields();

    Object getAsObject(DBFField field);
    Object getAsObject(String fieldName);
    Object getAsObject(int index);

    Object[] toRawRow();

    boolean isContainField(String fieldName);
}
