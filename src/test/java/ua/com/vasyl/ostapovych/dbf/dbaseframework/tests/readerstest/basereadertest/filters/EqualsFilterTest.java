package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest.filters;

import org.junit.Before;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFilterOperation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class EqualsFilterTest {

    private DBFReader<DBFRow> reader;

    @Before
    public void init(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        reader = DBASEFactory.dbf3().getDBFReader(dbfFile);
    }

    @Test
    public void testCharField() throws ParseException {

        DBFFilter filter = DBFFilter.getInstance("CHAR_F", DBFilterOperation.EQUAL,"char47");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(1,rows.size());
        DBFRow row = rows.get(0);
        assertEquals(row.getAsString("CHAR_F"),"char47");
        assertEquals(row.getAsInteger("NUM_INT_F").intValue(),48);
        assertEquals(row.getAsDouble("NUM_DBL_F"),5.904,0.0000001);
        assertEquals(row.getAsDate("DATE_F"),new SimpleDateFormat("dd.MM.yyy").parse("02.12.2024"));
        assertEquals(row.getAsFloat("FLOAT_F"),100.590f,0.00001);
        assertTrue(row.getAsLogic("LOGIC_F"));
    }

    @Test
    public void testFloatFields() throws ParseException {
        DBFFilter filter = DBFFilter.getInstance("FLOAT_F", DBFilterOperation.EQUAL,"100.590f");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(1,rows.size());
        DBFRow row = rows.get(0);
        assertEquals(row.getAsString("CHAR_F"),"char47");
        assertEquals(row.getAsInteger("NUM_INT_F").intValue(),48);
        assertEquals(row.getAsDouble("NUM_DBL_F"),5.904,0.0000001);
        assertEquals(row.getAsDate("DATE_F"),new SimpleDateFormat("dd.MM.yyy").parse("02.12.2024"));
        assertEquals(row.getAsFloat("FLOAT_F"),100.590f,0.00001);
        assertTrue(row.getAsLogic("LOGIC_F"));
    }

    @Test
    public void testLogic() {
        DBFFilter filter = DBFFilter.getInstance("LOGIC_F", DBFilterOperation.EQUAL,"true");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(44,rows.size());
    }

    @Test
    public void testNumeric() throws ParseException {
        DBFFilter filter = DBFFilter.getInstance("NUM_INT_F", DBFilterOperation.EQUAL,"48");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(1,rows.size());
        DBFRow row = rows.get(0);
        assertEquals(row.getAsString("CHAR_F"),"char47");
        assertEquals(row.getAsInteger("NUM_INT_F").intValue(),48);
        assertEquals(row.getAsDouble("NUM_DBL_F"),5.904,0.0000001);
        assertEquals(row.getAsDate("DATE_F"),new SimpleDateFormat("dd.MM.yyy").parse("02.12.2024"));
        assertEquals(row.getAsFloat("FLOAT_F"),100.590f,0.00001);
        assertTrue(row.getAsLogic("LOGIC_F"));
    }
}
