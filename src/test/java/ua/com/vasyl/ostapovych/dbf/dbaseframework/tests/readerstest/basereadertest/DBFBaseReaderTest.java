package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.basereadertest;

import org.junit.Assert;
import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.AbstractDBFReaderTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class DBFBaseReaderTest extends AbstractDBFReaderTest<DBFRow> {

    @Override
    protected void compare(DBFRow row, String[] examples) {
        assertEquals(examples[0],row.getAsString(0));
        assertEquals(Integer.parseInt(examples[1]),row.getAsInteger(1).intValue());
        assertEquals(Double.parseDouble(examples[2]),row.getAsDouble(2),0.00001);
        try {
            assertEquals(row.getAsDate(3),new SimpleDateFormat("dd.MM.yyyy").parse(examples[3]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(Float.parseFloat(examples[4]),row.getAsFloat(4),0.000001);
        assertEquals(row.getAsLogic(5),Boolean.parseBoolean(examples[5]));
    }

    @Test
    public void testBaseReaderDbfWithOneCharacterColumnMaxSizeAndMaxValue(){
        String dbfFile = getDBFTableByResourceName("dbf/dbf3/DBFOneCharacterFieldMaxSizeAndMaxValue.dbf");
        DBFReader<DBFRow> reader = getReader(dbfFile,null);
        reader.readAllRows();
        Assert.assertEquals(1,reader.getColumnCount());
        DBFField field = reader.getFields()[0];
        assertEquals(255,field.getSize());
        assertEquals(DBFType.CHARACTER, field.getDbfType());
        assertEquals(1,reader.readAllRows().size());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789012345",reader.readAllRows().get(0).getAsString(field));
    }

    @Override
    protected DBFReader<DBFRow> getReader(String dbfFile, DBFOptions options) {
        if (options != null) return DBASEFactory.dbf3(options).getDBFReader(dbfFile);
        return DBASEFactory.dbf3().getDBFReader(dbfFile);
    }
}
