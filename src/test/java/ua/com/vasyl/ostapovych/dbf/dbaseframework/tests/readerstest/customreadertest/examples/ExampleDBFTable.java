package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest.examples;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@DBFTable
@SuppressWarnings("unused")
public class ExampleDBFTable {

    @DBFColumn(name = "CHAR_F",type = DBFType.CHARACTER)
    private String char_f;

    @DBFColumn(name = "NUM_INT_F",type = DBFType.CHARACTER)
    private Integer num_int;

    @DBFColumn(name = "NUM_DBL_F",type = DBFType.NUMERIC)
    private Double num_double;

    @DBFColumn(name = "DATE_F",type = DBFType.DATE)
    private Date date;

    @DBFColumn(name = "FLOAT_F",type = DBFType.FLOAT)
    private Float float_f;

    @DBFColumn(name = "LOGIC_F",type = DBFType.LOGICAL)
    private Boolean logical;

    public String getChar_f() {
        return char_f;
    }

    public void setChar_f(String char_f) {
        this.char_f = char_f;
    }

    public Integer getNum_int() {
        return num_int;
    }

    public void setNum_int(Integer num_int) {
        this.num_int = num_int;
    }

    public Double getNum_double() {
        return num_double;
    }

    public void setNum_double(Double num_double) {
        this.num_double = num_double;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getFloat_f() {
        return float_f;
    }

    public void setFloat_f(Float float_f) {
        this.float_f = float_f;
    }

    public Boolean getLogical() {
        return logical;
    }

    public void setLogical(Boolean logical) {
        this.logical = logical;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleDBFTable that = (ExampleDBFTable) o;

        if (!Objects.equals(char_f, that.char_f)) return false;
        if (!Objects.equals(num_int, that.num_int)) return false;
        if (!Objects.equals(num_double, that.num_double)) return false;
        if (date != null ? !compareDate(that.date) : that.date != null) return false;
        if (!Objects.equals(float_f, that.float_f)) return false;
        return Objects.equals(logical, that.logical);
    }

    @Override
    public int hashCode() {
        int result = char_f != null ? char_f.hashCode() : 0;
        result = 31 * result + (num_int != null ? num_int.hashCode() : 0);
        result = 31 * result + (num_double != null ? num_double.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (float_f != null ? float_f.hashCode() : 0);
        result = 31 * result + (logical != null ? logical.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExampleDBFTable{" +
                "char_f='" + char_f + '\'' +
                ", num_int=" + num_int +
                ", num_double=" + num_double +
                ", date=" + date +
                ", float_f=" + float_f +
                ", logical=" + logical +
                '}';
    }

    private boolean compareDate(Date thatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date).equalsIgnoreCase(sdf.format(thatDate));
    }
}
