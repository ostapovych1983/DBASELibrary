package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

@SuppressWarnings({"SpellCheckingInspection","unused"})
@DBFTable
public class ExampleWithOneNumericIntegerColumn {
    @DBFColumn(name = "NIFIELD",type = DBFType.NUMERIC, size = 7)
    private Integer integer_field;

    public ExampleWithOneNumericIntegerColumn() {}

    public ExampleWithOneNumericIntegerColumn(Integer integer_field){
        this.integer_field = integer_field;
    }

    public Integer getInteger_field() {
        return integer_field;
    }

    public void setInteger_field(Integer integer_field) {
        this.integer_field = integer_field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithOneNumericIntegerColumn that = (ExampleWithOneNumericIntegerColumn) o;

        return integer_field != null ? integer_field.equals(that.integer_field) : that.integer_field == null;
    }

    @Override
    public int hashCode() {
        return integer_field != null ? integer_field.hashCode() : 0;
    }
}
