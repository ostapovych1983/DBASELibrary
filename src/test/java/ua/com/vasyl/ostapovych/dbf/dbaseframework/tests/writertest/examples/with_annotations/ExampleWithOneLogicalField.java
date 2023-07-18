package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.util.Objects;

@SuppressWarnings({"SpellCheckingInspection","unused"})
@DBFTable
public class ExampleWithOneLogicalField {
    @DBFColumn(name = "LFIELD", type = DBFType.LOGICAL, size = 1)
    private Boolean logical_field;

    public ExampleWithOneLogicalField(Boolean logical_field) {
        this.logical_field = logical_field;
    }
    public ExampleWithOneLogicalField(){}

    public Boolean getLogical_field() {
        return logical_field;
    }

    public void setLogical_field(Boolean logical_field) {
        this.logical_field = logical_field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithOneLogicalField that = (ExampleWithOneLogicalField) o;
        return Objects.equals(logical_field, that.logical_field);
    }

    @Override
    public int hashCode() {
        return logical_field != null ? logical_field.hashCode() : 0;
    }
}
