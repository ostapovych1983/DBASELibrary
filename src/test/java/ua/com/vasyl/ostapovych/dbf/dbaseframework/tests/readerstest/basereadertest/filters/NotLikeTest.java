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

public class NotLikeTest {

    private DBFReader<DBFRow> reader;

    @Before
    public void init(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        reader = DBASEFactory.dbf3().getDBFReader(dbfFile);
    }

    @Test
    public void testNotLikeChar(){
        DBFFilter filter = DBFFilter.getInstance("CHAR_F", DBFilterOperation.NOT_LIKE,"char47");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(99,rows.size());
    }

    @Test
    public void testNotLikeFloat(){
        DBFFilter filter = DBFFilter.getInstance("FLOAT_F", DBFilterOperation.NOT_LIKE,"100.59");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(99,rows.size());
    }

    @Test
    public void testNotLikeLogic(){
        DBFFilter filter = DBFFilter.getInstance("LOGIC_F", DBFilterOperation.NOT_LIKE,"t");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(0,rows.size());
    }

    @Test
    public void testNotLikeNumInt(){
        DBFFilter filter = DBFFilter.getInstance("NUM_INT_F", DBFilterOperation.NOT_LIKE,"99");
        List<DBFRow> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(99,rows.size());
    }

}
