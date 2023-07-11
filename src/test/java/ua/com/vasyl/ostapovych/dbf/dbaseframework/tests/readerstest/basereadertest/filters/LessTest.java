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

public class LessTest {

    private DBFReader<DBFRow> reader;

    @Before
    public void init(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        reader = DBASEFactory.dbf3().getDBFReader(dbfFile);
    }

    @Test
    public void testLessChar(){
        DBFFilter filter = DBFFilter.getInstance("CHAR_F", DBFilterOperation.LESS,"char47");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(0,rows.size());
    }

    @Test
    public void testLessFloat(){
        DBFFilter filter = DBFFilter.getInstance("FLOAT_F", DBFilterOperation.LESS,"100.590f");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(47,rows.size());
    }

    @Test
    public void testLessLogic(){

        DBFFilter filter = DBFFilter.getInstance("LOGIC_F", DBFilterOperation.LESS,"t");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(0,rows.size());
    }

    @Test
    public void testLessNumInt(){
        DBFFilter filter = DBFFilter.getInstance("NUM_INT_F", DBFilterOperation.LESS,"48");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(47,rows.size());
    }
}
