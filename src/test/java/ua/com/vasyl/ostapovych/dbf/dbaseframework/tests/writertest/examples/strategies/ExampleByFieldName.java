package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.strategies;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@DBFTable(accessMode = DBFGenerateStrategies.FIELDS_NAME)
@SuppressWarnings({"unused","SpellCheckingInspection"})
public class ExampleByFieldName {
    private String cField;
    private Float fField;
    private Integer iField;
    private Double dField;
    private Boolean bField;
    private Date dtField;

    public String getcField() {
        return cField;
    }

    public void setcField(String cField) {
        this.cField = cField;
    }

    public Float getfField() {
        return fField;
    }

    public void setfField(Float fField) {
        this.fField = fField;
    }

    public Integer getiField() {
        return iField;
    }

    public void setiField(Integer iField) {
        this.iField = iField;
    }

    public Double getdField() {
        return dField;
    }

    public void setdField(Double dField) {
        this.dField = dField;
    }

    public Boolean getbField() {
        return bField;
    }

    public void setbField(Boolean bField) {
        this.bField = bField;
    }

    public Date getDtField() {
        return dtField;
    }

    public void setDtField(Date dtField) {
        this.dtField = dtField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleByFieldName that = (ExampleByFieldName) o;

        if (!Objects.equals(cField, that.cField))
            return false;
        if (!Objects.equals(fField, that.fField)) return false;
        if (!Objects.equals(iField, that.iField)) return false;
        if (!Objects.equals(dField, that.dField)) return false;
        if (!Objects.equals(bField, that.bField)) return false;
        return dtField != null ? compareDate(that.dtField) : that.dtField == null;
    }

    @Override
    public int hashCode() {
        int result = cField != null ? cField.hashCode() : 0;
        result = 31 * result + (fField != null ? fField.hashCode() : 0);
        result = 31 * result + (iField != null ? iField.hashCode() : 0);
        result = 31 * result + (dField != null ? dField.hashCode() : 0);
        result = 31 * result + (bField != null ? bField.hashCode() : 0);
        result = 31 * result + (dtField != null ? dtField.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExampleByFieldName{" +
                "characterField='" + cField + '\'' +
                ", floatField=" + fField +
                ", integerField=" + iField +
                ", doubleField=" + dField +
                ", booleanField=" + bField +
                ", dateField=" + dtField +
                '}';
    }
    private boolean compareDate(Date thatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(this.dtField).equalsIgnoreCase(sdf.format(thatDate));
    }
}
