package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest.filters;

import org.junit.Before;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFilterOperation;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class MoreTest {

    private DBFReader<DBFRow> reader;

    @Before
    public void init(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        reader = DBASEFactory.dbf3().getDBFReader(dbfFile);
    }

    @Test
    public void testMoreChar(){
        DBFFilter filter = DBFFilter.getInstance("CHAR_F", DBFilterOperation.MORE_THAN,"char47");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(0,rows.size());
    }

    @Test
    public void testMoreFloat(){

        DBFFilter filter = DBFFilter.getInstance("FLOAT_F", DBFilterOperation.MORE_THAN,"100.590f");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(52,rows.size());
    }

    @Test
    public void testMoreLogic(){
        DBFFilter filter = DBFFilter.getInstance("LOGIC_F", DBFilterOperation.MORE_THAN,"t");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(0,rows.size());
    }

    @Test
    public void testNumberInt(){
        DBFFilter filter = DBFFilter.getInstance("NUM_INT_F", DBFilterOperation.MORE_THAN,"48");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(52,rows.size());
    }

}
