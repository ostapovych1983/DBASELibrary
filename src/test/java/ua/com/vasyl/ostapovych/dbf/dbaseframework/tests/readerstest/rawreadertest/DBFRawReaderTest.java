package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.rawreadertest;

import org.junit.Assert;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.AbstractDBFReaderTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DBFRawReaderTest extends AbstractDBFReaderTest<Object[]> {

    @Override
    protected void compare(Object[] row, String[] examples) {
        Assert.assertEquals(row[0],examples[0]);
        Assert.assertEquals(Integer.parseInt(String.valueOf(row[1])),Integer.parseInt(examples[1]));
        Assert.assertEquals(Double.parseDouble(String.valueOf(row[2])),Double.parseDouble(examples[2]),0.00001);
        try {
            Assert.assertEquals(row[3],new SimpleDateFormat("dd.MM.yyyy").parse(examples[3]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(Float.parseFloat(String.valueOf(row[4])),Float.parseFloat(examples[4]),0.000001);
        Assert.assertEquals(Boolean.parseBoolean(String.valueOf(row[5])),Boolean.parseBoolean(examples[5]));
    }

    @Override
    protected DBFReader<Object[]> getReader(String dbfFile, DBFOptions options) {
        if (options != null) return DBASEFactory.dbf3(options).getDBFRawReader(dbfFile);
        return DBASEFactory.dbf3().getDBFRawReader(dbfFile);
    }

}
