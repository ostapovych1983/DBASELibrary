package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.rawreadertest.filters;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.filters.AbstractContainsTest;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class ContainsTest extends AbstractContainsTest<Object[]> {
    @Override
    public DBFReader<Object[]> getReader() {
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        return DBASEFactory.dbf3().getDBFRawReader(dbfFile);
    }
}
