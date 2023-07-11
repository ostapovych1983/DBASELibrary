package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.text.SimpleDateFormat;
import java.util.Date;

@DBFTable
@SuppressWarnings("unused")
public class ExampleWithOneDateField {
    @SuppressWarnings("SpellCheckingInspection")
    @DBFColumn(name = "DFIELD", type = DBFType.DATE, size = 8)
    private Date date;

    public ExampleWithOneDateField(Date date) {
        this.date = date;
    }

    public ExampleWithOneDateField() {}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithOneDateField that = (ExampleWithOneDateField) o;

        return date != null ? compareDate(that.date) : that.date == null;
    }

    private boolean compareDate(Date thatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(this.date).equalsIgnoreCase(sdf.format(thatDate));
    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return "ExampleWithOneDateField{" +
                "date=" + sdf.format(date) +
                '}';
    }
}
