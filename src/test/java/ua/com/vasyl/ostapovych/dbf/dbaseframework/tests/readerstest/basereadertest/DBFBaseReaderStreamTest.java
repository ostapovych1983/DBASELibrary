package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest;

import org.junit.Assert;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;

import java.util.List;
import java.util.stream.Collectors;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class DBFBaseReaderStreamTest {

    @Test
    public void testMapFunction(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/StreamExample.dbf");
        DBFReader<DBFRow> reader = getReader(dbfFile);
        List<String> list = reader.stream().map(this::toDBFString).collect(Collectors.toList());
        Assert.assertEquals(100,list.size());
    }

    private String toDBFString(DBFRow dbfRow) {
        return dbfRow.getAsString(0);
    }

    protected DBFReader<DBFRow> getReader(String dbfFile) {
        return DBASEFactory.dbf3().getDBFReader(dbfFile);
    }
}
