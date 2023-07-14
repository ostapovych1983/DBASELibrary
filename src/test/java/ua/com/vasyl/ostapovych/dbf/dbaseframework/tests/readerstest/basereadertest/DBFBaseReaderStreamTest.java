package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest;

import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;

public class DBFBaseReaderStreamTest {

    @Test
    public void testMapFunction(){

    }

    protected DBFReader<DBFRow> getReader(String dbfFile, DBFOptions options) {
        if (options != null) return DBASEFactory.dbf3(options).getDBFReader(dbfFile);
        return DBASEFactory.dbf3().getDBFReader(dbfFile);
    }
}
