package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest;

import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.strategies.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.WriterRawRowsTest.createTemporaryFile;

@SuppressWarnings({"SpellCheckingInspection"})
public class WriterStrategyTest {
    @Test
    public void testAnnotatedGettersStrategy(){
        List<ExampleAnnotatedGetters> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleAnnotatedGetters row = new ExampleAnnotatedGetters();
            row.setBooleanField(true);
            row.setCharacterField("Row "+i);
            row.setDateField(new Date());
            row.setFloatField(i+0.123f);
            row.setDoubleField(i+0.987d);
            row.setIntegerField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleAnnotatedGetters.class);
        DBFReader<ExampleAnnotatedGetters> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(),ExampleAnnotatedGetters.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(6,reader.getColumnCount());

        assertEquals(new CharacterField("CFIELD",(short)50),reader.getFields()[0]);
        assertEquals(new FloatField("FFIELD",(short)10,(short)3),reader.getFields()[1]);
        assertEquals(new NumericField("NIFIELD",(short)10,(short)0),reader.getFields()[2]);
        assertEquals(new NumericField("NDFIELD",(short)20,(short)5),reader.getFields()[3]);
        assertEquals(new LogicalField("LFIELD"),reader.getFields()[4]);
        assertEquals(new DateField("DFIELD"),reader.getFields()[5]);

        List<ExampleAnnotatedGetters> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleAnnotatedGetters readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testAnnotatedFieldStrategy(){
        List<ExampleAnnotatedFields> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleAnnotatedFields row = new ExampleAnnotatedFields();
            row.setBooleanField(true);
            row.setCharacterField("Row "+i);
            row.setDateField(new Date());
            row.setFloatField(i+0.123f);
            row.setDoubleField(i+0.987d);
            row.setIntegerField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleAnnotatedFields.class);
        DBFReader<ExampleAnnotatedFields> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(),ExampleAnnotatedFields.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(6,reader.getColumnCount());

        assertEquals(new CharacterField("CFIELD",(short)50),reader.getFields()[0]);
        assertEquals(new FloatField("FFIELD",(short)10,(short)3),reader.getFields()[1]);
        assertEquals(new NumericField("NIFIELD",(short)10,(short)0),reader.getFields()[2]);
        assertEquals(new NumericField("NDFIELD",(short)20,(short)5),reader.getFields()[3]);
        assertEquals(new LogicalField("LFIELD"),reader.getFields()[4]);
        assertEquals(new DateField("DFIELD"),reader.getFields()[5]);

        List<ExampleAnnotatedFields> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleAnnotatedFields readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testGetterNameStrategy(){
        List<ExampleByGettersName> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleByGettersName row = new ExampleByGettersName();
            row.setbField(true);
            row.setcField("Row "+i);
            row.setDtField(new Date());
            row.setfField(i+0.123f);
            row.setdField(i+0.987d);
            row.setiField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleByGettersName.class);
        DBFReader<ExampleByGettersName> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(),ExampleByGettersName.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(6,reader.getColumnCount());

        assertTrue(isContainField(new CharacterField("cField",(short)50),reader.getFields()));
        assertTrue(isContainField(new FloatField("fField",(short)10,(short)3),reader.getFields()));
        assertTrue(isContainField(new NumericField("iField",(short)10,(short)0),reader.getFields()));
        assertTrue(isContainField(new NumericField("dField",(short)20,(short)5),reader.getFields()));
        assertTrue(isContainField(new LogicalField("bField"),reader.getFields()));
        assertTrue(isContainField(new DateField("dtField"),reader.getFields()));

        List<ExampleByGettersName> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleByGettersName readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testFieldNameStrategy(){
        List<ExampleByFieldName> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleByFieldName row = new ExampleByFieldName();
            row.setbField(true);
            row.setcField("Row "+i);
            row.setDtField(new Date());
            row.setfField(i+0.123f);
            row.setdField(i+0.987d);
            row.setiField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleByFieldName.class);
        DBFReader<ExampleByFieldName> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(),ExampleByFieldName.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(6,reader.getColumnCount());

        assertTrue(isContainField(new CharacterField("cField",(short)50),reader.getFields()));
        assertTrue(isContainField(new FloatField("fField",(short)10,(short)3),reader.getFields()));
        assertTrue(isContainField(new NumericField("iField",(short)10,(short)0),reader.getFields()));
        assertTrue(isContainField(new NumericField("dField",(short)20,(short)5),reader.getFields()));
        assertTrue(isContainField(new LogicalField("bField"),reader.getFields()));
        assertTrue(isContainField(new DateField("dtField"),reader.getFields()));

        List<ExampleByFieldName> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleByFieldName readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testAutomatedStrategyWithAnnotationSomeGetters(){
        List<ExampleAutomateAnnotatedGetters> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleAutomateAnnotatedGetters row = new ExampleAutomateAnnotatedGetters();
            row.setCharacterField("Row "+i);
            row.setFloatField(i+0.123f);
            row.setDoubleField(i+0.987d);
            row.setIntegerField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleAutomateAnnotatedGetters.class);
        DBFReader<ExampleAutomateAnnotatedGetters> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(),ExampleAutomateAnnotatedGetters.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(4,reader.getColumnCount());

        assertEquals(new CharacterField("CFIELD",(short)50),reader.getFields()[0]);
        assertEquals(new FloatField("FFIELD",(short)10,(short)3),reader.getFields()[1]);
        assertEquals(new NumericField("NIFIELD",(short)10,(short)0),reader.getFields()[2]);
        assertEquals(new NumericField("NDFIELD",(short)20,(short)5),reader.getFields()[3]);

        List<ExampleAutomateAnnotatedGetters> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleAutomateAnnotatedGetters readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testAutomatedStrategyWithAnnotationSomeFields(){
        List<ExampleAutomatedFields> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleAutomatedFields row = new ExampleAutomatedFields();
            row.setCharacterField("Row "+i);
            row.setFloatField(i+0.123f);
            row.setDoubleField(i+0.987d);
            row.setIntegerField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleAutomatedFields.class);
        DBFReader<ExampleAutomatedFields> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(),ExampleAutomatedFields.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(4,reader.getColumnCount());

        assertEquals(new CharacterField("CFIELD",(short)50),reader.getFields()[0]);
        assertEquals(new FloatField("FFIELD",(short)10,(short)3),reader.getFields()[1]);
        assertEquals(new NumericField("NIFIELD",(short)10,(short)0),reader.getFields()[2]);
        assertEquals(new NumericField("NDFIELD",(short)20,(short)5),reader.getFields()[3]);

        List<ExampleAutomatedFields> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleAutomatedFields readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testAutomatedStrategyWithAnnotatedFieldAndGetter(){
        List<ExampleAutomatedFieldAndGetterOverride> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleAutomatedFieldAndGetterOverride row = new ExampleAutomatedFieldAndGetterOverride();
            row.setCharacterField("Row "+i);
            row.setFloatField(i+0.123f);
            row.setDoubleField(i+0.987d);
            row.setIntegerField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleAutomatedFieldAndGetterOverride.class);
        DBFReader<ExampleAutomatedFieldAndGetterOverride> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(), ExampleAutomatedFieldAndGetterOverride.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(4,reader.getColumnCount());

        assertEquals(new CharacterField("FIELD1",(short)50),reader.getFields()[0]);
        assertEquals(new FloatField("FIELD2",(short)10,(short)3),reader.getFields()[1]);
        assertEquals(new NumericField("FIELD3",(short)10,(short)0),reader.getFields()[2]);
        assertEquals(new NumericField("FIELD4",(short)20,(short)5),reader.getFields()[3]);

        List<ExampleAutomatedFieldAndGetterOverride> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleAutomatedFieldAndGetterOverride readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();
    }

    @Test
    public void testAutomatedStrategyWithAnnotatedFieldAndGetterUnion(){
        List<ExampleAutomatedFieldAndGetterUnion> rows = new ArrayList<>();
        int rowsCount = 250;
        for (int i=0;i<rowsCount;i++){
            ExampleAutomatedFieldAndGetterUnion row = new ExampleAutomatedFieldAndGetterUnion();
            row.setCharacterField("Row "+i);
            row.setFloatField(i+0.123f);
            row.setDoubleField(i+0.987d);
            row.setIntegerField(i);
            rows.add(row);
        }
        File fileName = createTemporaryFile();
        DBFWriter dbfWriter = DBASEFactory.dbf3().getDBFWriter(fileName);
        dbfWriter.writeRows(rows, ExampleAutomatedFieldAndGetterUnion.class);
        DBFReader<ExampleAutomatedFieldAndGetterUnion> reader =
                DBASEFactory.dbf3().getCustomDBFReader
                        (fileName.getAbsolutePath(), ExampleAutomatedFieldAndGetterUnion.class);
        assertEquals(rowsCount,reader.getRowCount());
        assertEquals(4,reader.getColumnCount());

        assertEquals(new CharacterField("CFIELD",(short)50),reader.getFields()[0]);
        assertEquals(new FloatField("FFIELD",(short)10,(short)3),reader.getFields()[1]);
        assertEquals(new NumericField("NIFIELD",(short)10,(short)0),reader.getFields()[2]);
        assertEquals(new NumericField("NDFIELD",(short)20,(short)5),reader.getFields()[3]);

        List<ExampleAutomatedFieldAndGetterUnion> readRows = reader.readAllRows();
        assertEquals(rows.size(),readRows.size());
        for (ExampleAutomatedFieldAndGetterUnion readExample:readRows){
            assertTrue(rows.contains(readExample));
        }
        reader.close();

    }

    private boolean isContainField(DBFField cField, DBFField[] fields) {
        for (DBFField field:fields)
            if (field.getName().equalsIgnoreCase(cField.getName())) return true;
        return false;
    }
}


