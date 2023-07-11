package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters;

public class DBFFilter {
    private final String value;
    private final String fieldName;
    private final DBFilterOperation operation;

    public DBFFilter(String fieldName, DBFilterOperation operation, String value) {
        this.fieldName = fieldName;
        this.operation = operation;
        this.value = value;
    }

    public static DBFFilter getInstance(String fieldName, DBFilterOperation operation, String value) {
        return new DBFFilter(fieldName,operation,value);
    }

    public String getValue() {
        return value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public DBFilterOperation getOperation() {
        return operation;
    }

}
