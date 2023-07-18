package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.strategies;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType.*;

@SuppressWarnings({"unused","SpellCheckingInspection"})
public class ExampleAutomateAnnotatedGetters {
    private String characterField;
    private Float floatField;
    private Integer integerField;
    private Double doubleField;
    private Boolean booleanField = null;
    private Date dateField = null;

    @DBFColumn(name = "CFIELD",type = CHARACTER,size=50,positionOnDBFFile = 1)
    public String getCharacterField() {
        return characterField;
    }

    public void setCharacterField(String characterField) {
        this.characterField = characterField;
    }

    @DBFColumn(name = "FFIELD",type = FLOAT,size=10,decimalSize = 3,positionOnDBFFile = 2)
    public Float getFloatField() {
        return floatField;
    }

    public void setFloatField(Float floatField) {
        this.floatField = floatField;
    }

    @DBFColumn(name = "NIFIELD",type = NUMERIC,size=10, positionOnDBFFile = 3)
    public Integer getIntegerField() {
        return integerField;
    }

    public void setIntegerField(Integer integerField) {
        this.integerField = integerField;
    }

    @DBFColumn(name = "NDFIELD",type = NUMERIC,size=20,decimalSize = 5,positionOnDBFFile = 4)
    public Double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }

    public Boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(Boolean booleanField) {
        this.booleanField = booleanField;
    }


    public Date getDateField() {
        return dateField;
    }

    public void setDateField(Date dateField) {
        this.dateField = dateField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleAutomateAnnotatedGetters that = (ExampleAutomateAnnotatedGetters) o;

        if (!Objects.equals(characterField, that.characterField))
            return false;
        if (!Objects.equals(floatField, that.floatField)) return false;
        if (!Objects.equals(integerField, that.integerField)) return false;
        if (!Objects.equals(doubleField, that.doubleField)) return false;
        if (!Objects.equals(booleanField, that.booleanField)) return false;
        return dateField != null ? compareDate(that.dateField) : that.dateField == null;
    }

    @Override
    public int hashCode() {
        int result = characterField != null ? characterField.hashCode() : 0;
        result = 31 * result + (floatField != null ? floatField.hashCode() : 0);
        result = 31 * result + (integerField != null ? integerField.hashCode() : 0);
        result = 31 * result + (doubleField != null ? doubleField.hashCode() : 0);
        result = 31 * result + (booleanField != null ? booleanField.hashCode() : 0);
        result = 31 * result + (dateField != null ? dateField.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExampleAutomateAnnotatedGetters{" +
                "characterField='" + characterField + '\'' +
                ", floatField=" + floatField +
                ", integerField=" + integerField +
                ", doubleField=" + doubleField +
                ", booleanField=" + booleanField +
                ", dateField=" + dateField +
                '}';
    }
    private boolean compareDate(Date thatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(this.dateField).equalsIgnoreCase(sdf.format(thatDate));
    }
}
