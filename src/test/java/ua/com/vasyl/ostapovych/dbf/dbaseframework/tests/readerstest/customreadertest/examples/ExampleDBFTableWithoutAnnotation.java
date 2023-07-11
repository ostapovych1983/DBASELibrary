package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest.examples;

import java.util.Date;
@SuppressWarnings("unused")
public class ExampleDBFTableWithoutAnnotation {

    private String char_f;
    private Integer num_int_f;
    private Double num_dbl_f;
    private Date date_f;
    private Float float_f;
    private Boolean logic_f;

    public String getChar_f() {
        return char_f;
    }

    public void setChar_f(String char_f) {
        this.char_f = char_f;
    }

    public Integer getNum_int_f() {
        return num_int_f;
    }

    public void setNum_int_f(Integer num_int_f) {
        this.num_int_f = num_int_f;
    }

    public Double getNum_dbl_f() {
        return num_dbl_f;
    }

    public void setNum_dbl_f(Double num_dbl_f) {
        this.num_dbl_f = num_dbl_f;
    }

    public Date getDate_f() {
        return date_f;
    }

    public void setDate_f(Date date_f) {
        this.date_f = date_f;
    }

    public Float getFloat_f() {
        return float_f;
    }

    public void setFloat_f(Float float_f) {
        this.float_f = float_f;
    }

    public Boolean getLogic_f() {
        return logic_f;
    }

    public void setLogic_f(Boolean logic_f) {
        this.logic_f = logic_f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleDBFTableWithoutAnnotation that = (ExampleDBFTableWithoutAnnotation) o;

        if (char_f != null ? !char_f.equals(that.char_f) : that.char_f != null) return false;
        if (num_int_f != null ? !num_int_f.equals(that.num_int_f) : that.num_int_f != null) return false;
        if (num_dbl_f != null ? !num_dbl_f.equals(that.num_dbl_f) : that.num_dbl_f != null) return false;
        if (date_f != null ? !date_f.equals(that.date_f) : that.date_f != null) return false;
        if (float_f != null ? !float_f.equals(that.float_f) : that.float_f != null) return false;
        return logic_f != null ? logic_f.equals(that.logic_f) : that.logic_f == null;
    }

    @Override
    public int hashCode() {
        int result = char_f != null ? char_f.hashCode() : 0;
        result = 31 * result + (num_int_f != null ? num_int_f.hashCode() : 0);
        result = 31 * result + (num_dbl_f != null ? num_dbl_f.hashCode() : 0);
        result = 31 * result + (date_f != null ? date_f.hashCode() : 0);
        result = 31 * result + (float_f != null ? float_f.hashCode() : 0);
        result = 31 * result + (logic_f != null ? logic_f.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExampleDBFTableWithoutAnnotation{" +
                "char_f='" + char_f + '\'' +
                ", num_int_f=" + num_int_f +
                ", num_dbl_f=" + num_dbl_f +
                ", date_f=" + date_f +
                ", float_f=" + float_f +
                ", logic_f=" + logic_f +
                '}';
    }
}
