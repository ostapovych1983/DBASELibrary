package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.dbfrow;

import org.junit.Before;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.LogicalField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.DBFRowImpl;

import java.util.UUID;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.dbfrow.RowTestHelper.prepareFieldMap;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.*;

public class DBFRowTest {

    private DBFRow row;

    @Before
    public void init(){
        row = new DBFRowImpl(prepareFieldMap(),false);
    }

    @Test
    public void testDBFRow(){
        RowTestHelper.checkFirstColumn(row);
        RowTestHelper.checkSecondColumn(row);
        RowTestHelper.checkThirdColumn(row);
        RowTestHelper.checkFirthColumn(row);
        RowTestHelper.checkFifthColumn(row);
        RowTestHelper.checkSixesColumn(row);
    }

    @Test(expected = DBFIllegalFieldException.class)
    public void testDBFRowDBFieldNulls(){
        row.getAsDate((DBFField) null);
        row.getAsFloat((DBFField) null);
        row.getAsString((DBFField) null);
        row.getAsLogic((DBFField) null);
        row.getAsDouble((DBFField) null);
        row.getAsInteger((DBFField) null);
    }
    @Test(expected = DBFFieldNotFoundException.class)
    public void testDBFNotExistField(){
        DBFField field = new LogicalField(UUID.randomUUID().toString());
        row.getAsDate(field);
        row.getAsFloat(field);
        row.getAsString(field);
        row.getAsLogic(field);
        row.getAsDouble(field);
        row.getAsInteger(field);
    }

    @Test(expected = DBFIllegalFieldException.class)
    public void testDBFRowFieldNameNulls(){
        row.getAsDate((String) null);
        row.getAsFloat((String) null);
        row.getAsString((String) null);
        row.getAsLogic((String) null);
        row.getAsDouble((String) null);
        row.getAsInteger((String) null);
    }

    @Test(expected = DBFIllegalFieldException.class)
    public void testDBFRowNegativeIndexField(){
        row.getAsDate(-1);
        row.getAsFloat(-1);
        row.getAsString(-1);
        row.getAsLogic(-1);
        row.getAsDouble(-1);
        row.getAsInteger(-1);
    }

}
