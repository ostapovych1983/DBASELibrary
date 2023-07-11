package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.filters;

import org.junit.Before;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFFilter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.filters.DBFilterOperation;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractContainsTest<T> {
    private DBFReader<T> reader;

    @Before
    public void init(){
        reader = getReader();
    }
    public abstract DBFReader<T> getReader();

    @Test
    public void testContainsChar(){
        DBFFilter filter = DBFFilter.getInstance("CHAR_F", DBFilterOperation.CONTAINS,"char");
        List<T> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(100,rows.size());

    }

    @Test
    public void testContainsFloat(){
        DBFFilter filter = DBFFilter.getInstance("FLOAT_F", DBFilterOperation.CONTAINS,"100.59");
        List<T> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(1,rows.size());
    }

    @Test
    public void testContainsLogic(){
        DBFFilter filter = DBFFilter.getInstance("LOGIC_F", DBFilterOperation.CONTAINS,"t");
        List<T> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(0,rows.size());
    }

    @Test
    public void testContainsNumInt(){
        DBFFilter filter = DBFFilter.getInstance("NUM_INT_F", DBFilterOperation.CONTAINS,"4");
        List<T> rows = reader.readByFilter(filter);
        assertNotNull(rows);
        assertEquals(19,rows.size());
    }


}
