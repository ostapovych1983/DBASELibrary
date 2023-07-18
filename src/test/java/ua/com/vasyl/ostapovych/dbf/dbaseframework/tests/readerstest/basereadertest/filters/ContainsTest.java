package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest.filters;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.filters.AbstractContainsTest;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class ContainsTest extends AbstractContainsTest<DBFRow> {
    @Override
    public DBFReader<DBFRow> getReader() {

        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        return DBASEFactory.dbf3().getDBFReader(dbfFile);
    }

}
