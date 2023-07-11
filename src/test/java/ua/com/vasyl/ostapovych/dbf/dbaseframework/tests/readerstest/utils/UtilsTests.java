package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils;

import org.junit.Assert;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilsTests {
    public static   <T> void checkRowsAndColumns(DBFReader<T> reader, int expected_column_count, long expected_row_count) {
        assertEquals("Wrong read column count ",expected_column_count,reader.getColumnCount());
        assertEquals("Wrong read row count ",expected_row_count,reader.getRowCount());
        long countedRowLooped = 0;
        for (T dbfRow:reader){
            countedRowLooped++;
            if (countedRowLooped > expected_row_count) throw new RuntimeException("There more row than expected");
        }
        String message = String.format("DisSynchronization found method getRowCount give %s rows, but count loop way is %s",expected_row_count,countedRowLooped);
        assertEquals(message,expected_row_count,countedRowLooped);
        assertEquals("Wrong count number after read all rows in memory ",expected_row_count,reader.readAllRows().size());
    }

    public static String getDBFTableByResourceName(String resourceName) {
        URL url = UtilsTests.class.getClassLoader().getResource(resourceName);
        Assert.assertNotNull(resourceName);
        assertNotNull(url);
        return url.getPath();
    }



}
