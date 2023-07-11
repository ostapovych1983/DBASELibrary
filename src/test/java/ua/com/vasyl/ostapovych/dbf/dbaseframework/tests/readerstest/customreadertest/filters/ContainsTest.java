package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest.filters;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.filters.AbstractContainsTest;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest.examples.ExampleDBFTable;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class ContainsTest extends AbstractContainsTest<ExampleDBFTable> {
    @Override
    public DBFReader<ExampleDBFTable> getReader() {
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        return DBASEFactory.dbf3().getCustomDBFReader(dbfFile,ExampleDBFTable.class);
    }
}
