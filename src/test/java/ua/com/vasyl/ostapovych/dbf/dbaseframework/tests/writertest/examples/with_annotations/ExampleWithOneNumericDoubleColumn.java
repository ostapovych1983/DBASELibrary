package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.util.Objects;

@DBFTable
@SuppressWarnings("unused")
public class ExampleWithOneNumericDoubleColumn {
    @SuppressWarnings("SpellCheckingInspection")
    @DBFColumn(name = "NDFIELD",type = DBFType.NUMERIC,size = 7,decimalSize = 4)
    private Double double_field;

    public ExampleWithOneNumericDoubleColumn(Double double_field) {
        this.double_field = double_field;
    }

    public ExampleWithOneNumericDoubleColumn() {
    }

    public Double getDouble_field() {
        return double_field;
    }

    public void setDouble_field(Double double_field) {
        this.double_field = double_field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithOneNumericDoubleColumn that = (ExampleWithOneNumericDoubleColumn) o;

        return Objects.equals(double_field, that.double_field);
    }

    @Override
    public int hashCode() {
        return double_field != null ? double_field.hashCode() : 0;
    }
}
