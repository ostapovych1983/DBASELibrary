package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest;

import org.junit.Assert;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.abstractreaderstest.AbstractDBFReaderTest;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.customreadertest.examples.ExampleDBFTablePropertyAnnotation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomDBFReaderPropertyAnnotationTest extends AbstractDBFReaderTest<ExampleDBFTablePropertyAnnotation> {


    @Override
    protected void compare(ExampleDBFTablePropertyAnnotation row, String[] strings) {
        Assert.assertEquals(convert(strings),row);
    }

    @Override
    protected DBFReader<ExampleDBFTablePropertyAnnotation> getReader(String dbfFile, DBFOptions options) {
        if (options == null)
            return DBASEFactory.dbf3().getCustomDBFReader(dbfFile,ExampleDBFTablePropertyAnnotation.class);
        else
            return DBASEFactory.dbf3(options).getCustomDBFReader(dbfFile,ExampleDBFTablePropertyAnnotation.class);
    }

    private ExampleDBFTablePropertyAnnotation convert(String[] row) {
        ExampleDBFTablePropertyAnnotation res = new ExampleDBFTablePropertyAnnotation();
        res.setChar_f(row[0]);
        res.setNum_int(Integer.parseInt(row[1]));
        res.setNum_double(Double.parseDouble(row[2]));
        try {
            res.setDate(new SimpleDateFormat("dd.MM.yyyy").parse(row[3]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        res.setFloat_f(Float.parseFloat(row[4]));
        res.setLogical(Boolean.parseBoolean(row[5]));
        return res;
    }
}
