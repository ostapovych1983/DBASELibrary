package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest;

import org.junit.Assert;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFNoSuchRowException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.Instruments;

import java.util.Iterator;
import java.util.List;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.checkRowsAndColumns;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;


public abstract class AbstractDBFReaderTest<T> {
    @Test
    public void testBaseReadDbfWithoutDeletedRowAndNotReadDeletedRow(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        DBFOptions options = DBFOptions.defaultOptions();
        options.setReadDeleted(false);
        DBFReader<T> reader = getReader(dbfFile,options);
        checkRowsAndColumns(reader,6,100);
        checkDataRow(reader, Instruments.getExampleRows());
    }

    @Test
    public void testBaseReadDbfWithDeletedRowsAndNotReadDeletedRow(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructureWithDeleteRows.dbf");
        List<String[]> expectedRows = Instruments.getExampleRowsWithDeleted();
        DBFOptions options = DBFOptions.defaultOptions();
        options.setReadDeleted(false);
        DBFReader<T> reader = getReader(dbfFile,options);
        checkRowsAndColumns(reader,6,90);
        checkDataRow(reader,expectedRows);
    }

    @Test
    public void testBaseReadDbfWithAllDeletedRowsAndNotReadDeletedRow(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructureWithAllDeleteRows.dbf");
        DBFOptions options = DBFOptions.defaultOptions();
        options.setReadDeleted(false);
        DBFReader<T> reader = getReader(dbfFile,options);
        checkRowsAndColumns(reader,6,0);
    }

    @Test
    public void testBaseReadDbfWithDeletedRowsAndNotReadDeletedRowEOF(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructureWithDeleteRowsInEOF.dbf");
        List<String[]> expectedRows = Instruments.getExampleRowsWithDeletedEOF();
        DBFOptions options = DBFOptions.defaultOptions();
        options.setReadDeleted(false);
        DBFReader<T> reader = getReader(dbfFile,options);
        checkRowsAndColumns(reader,6,90);
        checkDataRow(reader,expectedRows);
    }

    @Test(expected = DBFNoSuchRowException.class)
    public void testBaseReaderDbfWithoutDeletedRowUsingIteratorWithoutUsingHasNext(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructure.dbf");
        DBFReader<T> reader = getReader(dbfFile,null);
        Iterator<T> iterator = reader.iterator();
        int count=0;
        for (int i=0;i<101;i++){
            count++;
            iterator.next();
            Assert.assertFalse(count>100);
        }
    }

    @Test
    public void testReadDBFFileWithDosCodePage(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/common/DBFCommonStructureDosCyrillic.dbf");
        List<String[]> examples = Instruments.getExampleRowsDosCodePage();
        DBFOptions options = DBFOptions.defaultOptions();
        options.setCodePage(DBFCodePage.readByInternationCode(866));
        DBFReader<T> reader = getReader(dbfFile,options);
        checkRowsAndColumns(reader,6,1);
        checkDataRow(reader,examples);
    }


    protected abstract void compare(T row, String[] strings);
    protected abstract DBFReader<T> getReader(String dbfFile, DBFOptions options);

    private void checkDataRow(DBFReader<T> reader, List<String[]> exampleRows) {
        List<T> rows = reader.readAllRows();
        for (int i=0;i<rows.size();i++){
            T row = rows.get(i);
            compare(row,exampleRows.get(i));
        }
    }



}
