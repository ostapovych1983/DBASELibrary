package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest.filters;

import org.junit.Before;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFilterOperation;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class NotEqualsTest {
    private DBFReader<DBFRow> reader;

    @Before
    public void init(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        reader = DBASEFactory.dbf3().getDBFReader(dbfFile);
    }
    @Test
    public void testNotEqualChar(){
        DBFFilter filter = DBFFilter.getInstance("CHAR_F", DBFilterOperation.NOT_EQUAL,"char47");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(99,rows.size());
    }

    @Test
    public void testNotEqualFloat(){
        DBFFilter filter = DBFFilter.getInstance("FLOAT_F", DBFilterOperation.NOT_EQUAL,"100.590f");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(99,rows.size());
    }

    @Test
    public void testNotEqualLogic(){
        DBFFilter filter = DBFFilter.getInstance("LOGIC_F", DBFilterOperation.NOT_EQUAL,"true");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(56,rows.size());
    }

    @Test
    public void testNotEqualNumberInt(){
        DBFFilter filter = DBFFilter.getInstance("NUM_INT_F", DBFilterOperation.NOT_EQUAL,"48");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(99,rows.size());
    }

}
