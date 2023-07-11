package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFMap;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.*;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static java.lang.Boolean.parseBoolean;

@SuppressWarnings({"unused"})
public class DBFRowImpl implements DBFRow {

    private final DBFMap dbfMap;
    private final boolean deleted;

    public DBFRowImpl(DBFMap dbfMap,
                      boolean deleted) {
        this.deleted = deleted;
        this.dbfMap = dbfMap;

    }

    @Override
    public Date getAsDate(DBFField field) {
        checkExistField(field);
        return (Date) dbfMap.get(field);
    }

    @Override
    public Float getAsFloat(DBFField field) {
        checkExistField(field);
        return Float.parseFloat(String.valueOf(dbfMap.get(field)));
    }

    @Override
    public String getAsString(DBFField field) {
        checkExistField(field);
        return String.valueOf(dbfMap.get(field));
    }

    @Override
    public Boolean getAsLogic(DBFField field) {
        checkExistField(field);
        Object val = dbfMap.get(field);
        if (val instanceof Boolean) return (Boolean) val;
        else return parseBoolean(String.valueOf(val));
    }

    @Override
    public Double getAsDouble(DBFField field) {
        checkExistField(field);
        return Double.parseDouble(String.valueOf(dbfMap.get(field)));
    }

    @Override
    public Integer getAsInteger(DBFField field) {
        checkExistField(field);
        return Integer.parseInt(String.valueOf(dbfMap.get(field)));
    }


    @Override
    public Date getAsDate(String fieldName) {
        checkExistField(fieldName);
        return (Date) dbfMap.get(dbfMap.getFieldByName(fieldName));
    }


    @Override
    public Float getAsFloat(String fieldName) {
        checkExistField(fieldName);
        return getAsFloat(dbfMap.getFieldByName(fieldName));
    }

    @Override
    public String getAsString(String fieldName) {
        checkExistField(fieldName);
        return getAsString(dbfMap.getFieldByName(fieldName));
    }

    @Override
    public Boolean getAsLogic(String fieldName) {
        checkExistField(fieldName);
        return getAsLogic(dbfMap.getFieldByName(fieldName));
    }

    @Override
    public Double getAsDouble(String fieldName) {
        checkExistField(fieldName);
        return getAsDouble(dbfMap.getFieldByName(fieldName));
    }

    @Override
    public Integer getAsInteger(String fieldName) {
        checkExistField(fieldName);
        return getAsInteger(dbfMap.getFieldByName(fieldName));
    }

    @Override
    public Date getAsDate(int index) {
        checkExistField(index);
        return getAsDate(dbfMap.getFieldByIndex(index));
    }



    @Override
    public Float getAsFloat(int index) {
        return getAsFloat(dbfMap.getFieldByIndex(index));
    }

    @Override
    public String getAsString(int index) {
        return getAsString(dbfMap.getFieldByIndex(index));
    }

    @Override
    public Boolean getAsLogic(int index) {
        return getAsLogic(dbfMap.getFieldByIndex(index));
    }

    @Override
    public Double getAsDouble(int index) {
        return getAsDouble(dbfMap.getFieldByIndex(index));
    }

    @Override
    public Integer getAsInteger(int index) {
        return getAsInteger(dbfMap.getFieldByIndex(index));
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Set<DBFField> fields() {
        return dbfMap.keySet();
    }

    @Override
    public Object getAsObject(DBFField field) {
        return dbfMap.get(field);
    }

    @Override
    public Object getAsObject(String fieldName) {
        return dbfMap.get(dbfMap.getFieldByName(fieldName));
    }

    @Override
    public Object getAsObject(int index) {
        return dbfMap.get(dbfMap.getFieldByIndex(index));
    }

    public Object[] toRawRow(){
        return dbfMap.generateRawRow();
    }

    @Override
    public boolean isContainField(String fieldName) {
        DBFField [] fields = fields().toArray(new DBFField[0]);
        for (DBFField field:fields){
            if (field.getName().equalsIgnoreCase(fieldName)) return true;
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{isDeleted = "+isDeleted());


        for (DBFField field: dbfMap.keySet()){
            Object val = dbfMap.get(field);
            if (val instanceof Date){
                val = new SimpleDateFormat("dd-MM-yyy").format((Date)val);
            }
            if (val instanceof String){
                val = ((String)val).trim();
            }
            String fieldName = field.getName();
            res.append("\"")
                    .append(fieldName)
                    .append("\" : \"")
                    .append((String.valueOf(val)).trim())
                    .append("\" ,");

        }

        res.deleteCharAt(res.lastIndexOf(",")).append("}");
        return res.toString();
    }

    private void checkExistField(DBFField field) {
        if (field == null)
            throw new DBFIllegalFieldException("Parameter field cannot be null ");
        if (!dbfMap.containsKey(field))
            throw new DBFFieldNotFoundException("Field "+field+" not found");
    }

    private void checkExistField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty())
            throw new DBFIllegalFieldException("Parameter field cannot be null or empty ");
        DBFField field = dbfMap.getFieldByName(fieldName);
        if (field == null) throw new DBFFieldNotFoundException("Field "+fieldName+" not found");
        checkExistField(field);
    }

    private void checkExistField(int index) {
        if (index <0) throw new DBFIllegalFieldException("Index of field cannot be negative");
        if (index >= dbfMap.size()) throw new DBFFileOpenException("Cannot find field with index");
    }


}
