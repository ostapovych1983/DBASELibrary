package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.strategies;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType.*;

@SuppressWarnings("ALL")
public class ExampleAutomatedFieldAndGetterUnion {
    @DBFColumn(name = "CFIELD",type = CHARACTER,size=50,positionOnDBFFile = 1)
    private String characterField;
    @DBFColumn(name = "FFIELD",type = FLOAT,size=10,decimalSize = 3,positionOnDBFFile = 2)
    private Float floatField;
    private Integer integerField;
    private Double doubleField;
    private Boolean booleanField;
    private Date dateField;


    public String getCharacterField() {
        return characterField;
    }

    public void setCharacterField(String characterField) {
        this.characterField = characterField;
    }


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

        ExampleAutomatedFieldAndGetterUnion that = (ExampleAutomatedFieldAndGetterUnion) o;

        if (characterField != null ? !characterField.equals(that.characterField) : that.characterField != null)
            return false;
        if (floatField != null ? !floatField.equals(that.floatField) : that.floatField != null) return false;
        if (integerField != null ? !integerField.equals(that.integerField) : that.integerField != null) return false;
        if (doubleField != null ? !doubleField.equals(that.doubleField) : that.doubleField != null) return false;
        if (booleanField != null ? !booleanField.equals(that.booleanField) : that.booleanField != null) return false;
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
        return "ExampleAutomatedFieldAndGetterUnion{" +
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
