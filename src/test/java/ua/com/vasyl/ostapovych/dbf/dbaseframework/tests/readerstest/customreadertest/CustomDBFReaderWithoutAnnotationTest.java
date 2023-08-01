package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest;

import org.junit.Assert;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.AbstractDBFReaderTest;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest.examples.ExampleDBFTableWithoutAnnotation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomDBFReaderWithoutAnnotationTest extends AbstractDBFReaderTest<ExampleDBFTableWithoutAnnotation> {

    @Override
    protected void compare(ExampleDBFTableWithoutAnnotation row, String[] strings) {
        Assert.assertEquals(convert(strings),row);
    }

    @Override
    protected DBFReader<ExampleDBFTableWithoutAnnotation> getReader(String dbfFile, DBFOptions options) {
        if (options == null)
             return DBASEFactory.dbf3().getCustomDBFReader(dbfFile,ExampleDBFTableWithoutAnnotation.class);
        else
            return DBASEFactory.dbf3(options).getCustomDBFReader(dbfFile,ExampleDBFTableWithoutAnnotation.class);
    }

    private ExampleDBFTableWithoutAnnotation convert(String[] row) {
        ExampleDBFTableWithoutAnnotation res = new ExampleDBFTableWithoutAnnotation();
        res.setChar_f(row[0]);
        res.setNum_int_f(Integer.parseInt(row[1]));
        res.setNum_dbl_f(Double.parseDouble(row[2]));
        try {
            res.setDate_f(new SimpleDateFormat("dd.MM.yyyy").parse(row[3]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        res.setFloat_f(Float.parseFloat(row[4]));
        res.setLogic_f(Boolean.parseBoolean(row[5]));
        return res;
    }


}
