package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

  class __FilterUtils__ {

     boolean checkFilter(DBFRow row, String val, DBFFilter filter) {
        if (row == null) return false;
        Set<DBFField> fields = row.fields();
        DBFField field = null;
        for (DBFField f:fields){
            if (f.getName().equalsIgnoreCase(filter.getFieldName())){
                field = f;
                break;
            }
        }
        if (field == null) return false;
        switch (field.getDbfType()){
            case LOGICAL: return _checkLogicalFilter(row,val,field,filter);
            case NUMERIC: return _checkNumericFilter(row,val,field,filter);
            case FLOAT: return _checkFloatFilter(row,val,field,filter);
            case CHARACTER: return _checkCharacterFilter(row,val,field,filter);
            case DATE: return _checkDateField(row,val,field,filter);
        }
        return false;
    }

      boolean _checkDateField(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        try {
            switch (filter.getOperation()){
                case EQUAL:return new SimpleDateFormat("dd.MM.yyy").parse(val).equals(row.getAsDate(filteredField));
                case NOT_EQUAL: return !new SimpleDateFormat("dd.MM.yyy").parse(val).equals(row.getAsDate(filteredField));

            }
            return false;
        } catch (ParseException e) {
            return false;
        }
    }

    boolean _checkCharacterFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()){
            case EQUAL: return val.equalsIgnoreCase(row.getAsString(filteredField));
            case NOT_EQUAL: return !val.equalsIgnoreCase(row.getAsString(filteredField));
            case CONTAINS: return  row.getAsString(filteredField).contains(val);
            case LIKE:   return row.getAsString(filteredField).matches(val.replace('%','*'));
            case NOT_LIKE: return !row.getAsString(filteredField).matches(val.replace('%','*'));
        }
        return false;
    }

    boolean _checkFloatFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()) {
            case EQUAL: return Float.parseFloat(val) == row.getAsFloat(filteredField);
            case NOT_EQUAL: return Float.parseFloat(val) != row.getAsFloat(filteredField);
            case LESS: return  row.getAsFloat(filteredField) < Float.parseFloat(val);
            case MORE_THAN:return  row.getAsFloat(filteredField) > Float.parseFloat(val);
            case CONTAINS: return  row.getAsString(filteredField).contains(val);
            case LIKE:  return row.getAsString(filteredField).matches(val.replace('%','*'));
            case NOT_LIKE: return !row.getAsString(filteredField).matches(val.replace('%','*'));
        }
        return false;
    }

    boolean _checkNumericFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()) {
            case EQUAL:   {
                if (filteredField.getDecimalSize() > 0)
                    return Double.parseDouble(val) == row.getAsDouble(filteredField);
                else
                    return Integer.parseInt(val) == row.getAsInteger(filteredField);
            }
            case NOT_EQUAL: {
                if (filteredField.getDecimalSize() > 0)
                    return Double.parseDouble(val) != row.getAsDouble(filteredField);
                else
                    return Integer.parseInt(val) != row.getAsInteger(filteredField);
            }

            case LESS: {
                if (filteredField.getDecimalSize() > 0)
                    return  row.getAsDouble(filteredField)  <  Double.parseDouble(val);
                else
                    return row.getAsInteger(filteredField) < Integer.parseInt(val);
            }
            case MORE_THAN: {
                if (filteredField.getDecimalSize() > 0)
                    return  row.getAsDouble(filteredField)  >  Double.parseDouble(val);
                else
                    return row.getAsInteger(filteredField) > Integer.parseInt(val);
            }
            case CONTAINS:{
                    return  row.getAsString(filteredField).contains(val);
            }
            case LIKE:{
                return row.getAsString(filteredField).matches(val.replace('%','*'));
            }
            case NOT_LIKE:{
                return !row.getAsString(filteredField).matches(val.replace('%','*'));
            }
        }
        return false;
    }

    boolean _checkLogicalFilter(DBFRow row, String val, DBFField filteredField, DBFFilter filter) {
        switch (filter.getOperation()){
            case EQUAL: return row.getAsLogic(filteredField) == Boolean.parseBoolean(val);
            case NOT_EQUAL: return row.getAsLogic(filteredField) != Boolean.parseBoolean(val);
        }
        return false;
    }

}
