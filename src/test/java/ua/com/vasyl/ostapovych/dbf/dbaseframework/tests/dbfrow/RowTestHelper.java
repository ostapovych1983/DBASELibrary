package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.dbfrow;


import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFMap;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class RowTestHelper {

        private static final DBFField CHARACTER_FIELD = new CharacterField("CHAR_F",(byte)14);
        private static final DBFField NUMBER_INT_FIELD =  new NumericField("NUM_INT_F",(byte)4,(byte)0);
        private static final DBFField NUMBER_DOUBLE_FIELD = new NumericField("NUM_DBL_F",(byte)0,(byte)3);
        private static final DBFField DATE_FILED = new DateField("DATE_F");
        private static final DBFField FLOAT_FIELD = new FloatField("FLOAT_F",(short)10,(short)3);
        private static final DBFField LOGIC_FIELD = new LogicalField("LOGIC_F");

    static void checkFirstColumn(DBFRow row) {
        DBFField dbfField = CHARACTER_FIELD;
        String name = dbfField.getName();
        assertEquals("char0",row.getAsString(0));
        assertEquals("char0",row.getAsString(name));
        assertEquals("char0",row.getAsString(dbfField));
    }

     static void checkSecondColumn(DBFRow row) {
        DBFField dbfField = NUMBER_INT_FIELD;
        String name = dbfField.getName();
        assertEquals("1",row.getAsString(1));
        assertEquals("1",row.getAsString(name));
        assertEquals("1",row.getAsString(dbfField));
        assertEquals(1,row.getAsInteger(1).intValue());
        assertEquals(1,row.getAsInteger(name).intValue());
        assertEquals(1,row.getAsInteger(dbfField).intValue());
        assertEquals(1.000, row.getAsDouble(1),0.0000001);
        assertEquals(1.000, row.getAsDouble(name),0.0000001);
        assertEquals(1.000, row.getAsDouble(dbfField),0.00000001);
        assertEquals(1.000F, row.getAsFloat(1),0.0000001);
        assertEquals(1.000F, row.getAsFloat(name),0.0000001);
        assertEquals(1.000F, row.getAsFloat(dbfField),0.00000001);
    }

     static void checkThirdColumn(DBFRow row) {
        DBFField dbfField = NUMBER_DOUBLE_FIELD;
        String name = dbfField.getName();
        assertEquals(0.123D, row.getAsDouble(2),0.00001);
        assertEquals(0.123D, row.getAsDouble(name),0.00001);
        assertEquals(0.123D, row.getAsDouble(dbfField),0.00001);
        assertEquals(0.123D, row.getAsFloat(2),0.00001);
        assertEquals(0.123D, row.getAsFloat(name),0.00001);
        assertEquals(0.123D, row.getAsFloat(dbfField),0.00001);
        assertEquals("0.123", row.getAsString(name));
        assertEquals("0.123", row.getAsString(2));
        assertEquals("0.123", row.getAsString(dbfField));
    }

     static void checkFirthColumn(DBFRow row) {
        DBFField dbfField = DATE_FILED;
        String name = dbfField.getName();
        Calendar calendar = getDate();
        assertEquals(calendar.getTime(),row.getAsDate(3));
        assertEquals(calendar.getTime(),row.getAsDate(name));
        assertEquals(calendar.getTime(),row.getAsDate(dbfField));
    }

     static void checkFifthColumn(DBFRow row) {
        DBFField dbfField = FLOAT_FIELD;
        String name = dbfField.getName();
        assertEquals(100.012D, row.getAsDouble(4),0.00001);
        assertEquals(100.012D, row.getAsDouble(name),0.00001);
        assertEquals(100.012D, row.getAsDouble(dbfField),0.00001);
        assertEquals(100.012F, row.getAsFloat(4),0.00001);
        assertEquals(100.012F, row.getAsFloat(name),0.00001);
        assertEquals(100.012F, row.getAsFloat(dbfField),0.00001);
        assertEquals("100.012", row.getAsString(name));
        assertEquals("100.012", row.getAsString(4));
        assertEquals("100.012", row.getAsString(dbfField));
    }

     static void checkSixesColumn(DBFRow row) {
        DBFField dbfField = LOGIC_FIELD;
        String name = dbfField.getName();
        assertEquals("true",row.getAsString(5));
        assertEquals("true",row.getAsString(name));
        assertEquals("true",row.getAsString(dbfField));
        assertTrue(row.getAsLogic(5));
        assertTrue(row.getAsLogic(name));
        assertTrue(row.getAsLogic(dbfField));
    }

    static DBFMap prepareFieldMap(){
        DBFMap res = new DBFMap();
        res.put(CHARACTER_FIELD,"char0");
        res.put(NUMBER_INT_FIELD,1);
        res.put(NUMBER_DOUBLE_FIELD,0.123D);
        res.put(DATE_FILED,getDate().getTime());
        res.put(FLOAT_FIELD,100.012F);
        res.put(LOGIC_FIELD,true);
        return res;
    }

    private static Calendar getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,2);
        calendar.set(Calendar.YEAR,2021);
        calendar.set(Calendar.AM_PM,Calendar.AM);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }
}
