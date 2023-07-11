package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations.ExampleWithOneFloatColumn.FLOAT_EXAMPLE_TABLE_NAME;

@SuppressWarnings("ALL")
@DBFTable(name = FLOAT_EXAMPLE_TABLE_NAME)
public class ExampleWithOneFloatColumn {
    public static final String FLOAT_EXAMPLE_TABLE_NAME = "FloatExampleTable";

    @DBFColumn(name = "FFIELD", type = DBFType.FLOAT,size = 6,decimalSize = 3)
    private float float_field;

    public ExampleWithOneFloatColumn() {}

    public ExampleWithOneFloatColumn(float f) {
        this.float_field = f;
    }

    public float getFloat_field() {
        return float_field;
    }

    public void setFloat_field(float float_field) {
        this.float_field = float_field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithOneFloatColumn that = (ExampleWithOneFloatColumn) o;

        return Float.compare(that.float_field, float_field) == 0;
    }

    @Override
    public int hashCode() {
        return (float_field != +0.0f ? Float.floatToIntBits(float_field) : 0);
    }
}
