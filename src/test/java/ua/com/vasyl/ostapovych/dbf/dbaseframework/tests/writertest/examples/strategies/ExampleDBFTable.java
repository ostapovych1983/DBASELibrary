package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.strategies;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.util.Date;

public class ExampleDBFTable {

    @DBFColumn(name = "strColumn",size = 50, type = DBFType.CHARACTER)
    private String stringColumn;
    @DBFColumn(name = "dateColumn",positionOnDBFFile = 1,type = DBFType.DATE)
    private Date dateColumn;
    @DBFColumn(name = "numColumn",size = 50,decimalSize = 3,positionOnDBFFile = 2,type = DBFType.NUMERIC)
    private Double numericColumn;

    @DBFColumn(name = "boolColumn",positionOnDBFFile = 3,type = DBFType.LOGICAL)
    private Boolean booleanColumn;

    public String getStringColumn() {
        return stringColumn;
    }

    public void setStringColumn(String stringColumn) {
        this.stringColumn = stringColumn;
    }

    public Date getDateColumn() {
        return dateColumn;
    }

    public void setDateColumn(Date dateColumn) {
        this.dateColumn = dateColumn;
    }

    public Double getNumericColumn() {
        return numericColumn;
    }

    public void setNumericColumn(Double numericColumn) {
        this.numericColumn = numericColumn;
    }

    public Boolean getBooleanColumn() {
        return booleanColumn;
    }

    public void setBooleanColumn(Boolean booleanColumn) {
        this.booleanColumn = booleanColumn;
    }
}
