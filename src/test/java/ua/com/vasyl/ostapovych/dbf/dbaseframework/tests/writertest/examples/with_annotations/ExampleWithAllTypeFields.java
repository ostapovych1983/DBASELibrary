package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings({"SpellCheckingInspection","unused"})
@DBFTable

public class ExampleWithAllTypeFields {
    @DBFColumn(name = "CFIELD",type = DBFType.CHARACTER,size = 50)
    private String character_field = "TEST";
    @DBFColumn(name = "FFIELD",type = DBFType.FLOAT,size = 20,decimalSize = 5)
    private Float float_field = 11.5246f;
    @DBFColumn(name = "NIFIELD",type = DBFType.NUMERIC,size = 10)
    private Integer numeric_integer_field = 10254;
    @DBFColumn(name = "NDFIELD",type = DBFType.NUMERIC,size = 10,decimalSize = 5)
    private Double numeric_double_field = 254.56895;
    @DBFColumn(name = "DFIELD",type = DBFType.DATE)
    private Date date_field = new Date();
    @DBFColumn(name = "LFIELD",type = DBFType.LOGICAL)
    private Boolean logicalField = true;

    public String getCharacter_field() {
        return character_field;
    }

    public void setCharacter_field(String character_field) {
        this.character_field = character_field;
    }

    public Float getFloat_field() {
        return float_field;
    }

    public void setFloat_field(Float float_field) {
        this.float_field = float_field;
    }

    public Integer getNumeric_integer_field() {
        return numeric_integer_field;
    }

    public void setNumeric_integer_field(Integer numeric_integer_field) {
        this.numeric_integer_field = numeric_integer_field;
    }

    public Double getNumeric_double_field() {
        return numeric_double_field;
    }

    public void setNumeric_double_field(Double numeric_double_field) {
        this.numeric_double_field = numeric_double_field;
    }

    public Date getDate_field() {
        return date_field;
    }

    public void setDate_field(Date date_field) {
        this.date_field = date_field;
    }

    public Boolean getLogicalField() {
        return logicalField;
    }

    public void setLogicalField(Boolean logicalField) {
        this.logicalField = logicalField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithAllTypeFields that = (ExampleWithAllTypeFields) o;

        if (!Objects.equals(character_field, that.character_field))
            return false;
        if (!Objects.equals(float_field, that.float_field)) return false;
        if (!Objects.equals(numeric_integer_field, that.numeric_integer_field))
            return false;
        if (!Objects.equals(numeric_double_field, that.numeric_double_field))
            return false;
        if (date_field != null )
            if ( !compareDate(that.date_field)) return false;
        return Objects.equals(logicalField, that.logicalField);
    }

    @Override
    public int hashCode() {
        int result = character_field != null ? character_field.hashCode() : 0;
        result = 31 * result + (float_field != null ? float_field.hashCode() : 0);
        result = 31 * result + (numeric_integer_field != null ? numeric_integer_field.hashCode() : 0);
        result = 31 * result + (numeric_double_field != null ? numeric_double_field.hashCode() : 0);
        result = 31 * result + (date_field != null ? date_field.hashCode() : 0);
        result = 31 * result + (logicalField != null ? logicalField.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExampleWithAllTypeFields{" +
                "character_field='" + character_field + '\'' +
                ", float_field=" + float_field +
                ", numeric_integer_field=" + numeric_integer_field +
                ", numeric_double_field=" + numeric_double_field +
                ", date_field=" + date_field +
                ", logicalField=" + logicalField +
                '}';
    }

    private boolean compareDate(Date thatDate) {
        if (thatDate == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(this.date_field).equalsIgnoreCase(sdf.format(thatDate));
    }

}
