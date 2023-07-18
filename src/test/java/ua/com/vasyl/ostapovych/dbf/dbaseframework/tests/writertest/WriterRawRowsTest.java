package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest;

import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.DBASEFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.*;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings({"SpellCheckingInspection"})
public class WriterRawRowsTest {
    @Test
    public void testWriteRawDBFRow(){
        DBFField [] fields = new DBFField[6];
        fields[0] = new CharacterField("CFIELD",(short)30);
        fields[1] = new FloatField("FFIELD",15,3);
        fields[2] = new NumericField("NIFIELD",5,0);
        fields[3] = new NumericField("NDFIELD",10,3);
        fields[4] = new DateField("DFIELD");
        fields[5] = new LogicalField("LFIELD");

        Object[][]  rows  = new Object[5][6];
        for (int i=0;i<rows.length;i++){
            rows[i][0] = "Field"+i;
            rows[i][1] = 0.123f+i;
            rows[i][2] = 1000+i;
            rows[i][3] = 2000.987+i;
            rows[i][4] = new Date();
            rows[i][5] = true;
        }
        File fileName = createTemporaryFile();
        DBFWriter writer = DBASEFactory.dbf3().getDBFWriter(fileName);
        writer.writeRows(fields,rows);

        DBFReader<Object[]> reader = DBASEFactory.dbf3().getDBFRawReader(fileName.getAbsolutePath());
        List<Object[]> readRows = reader.readAllRows();
        for (Object[] row:rows){
            assertTrue(isContainsRow(row,readRows));
        }
        reader.close();
    }

    @Test
    public void testWriteRawDBFRowWithEmptyRow(){
        DBFField [] fields = new DBFField[6];
        fields[0] = new CharacterField("CFIELD",(short)30);
        fields[1] = new FloatField("FFIELD",15,3);
        fields[2] = new NumericField("NIFIELD",5,0);
        fields[3] = new NumericField("NDFIELD",10,3);
        fields[4] = new DateField("DFIELD");
        fields[5] = new LogicalField("LFIELD");

        Object[][]  rows  = new Object[1][6];
        rows[0][0] = null;
        rows[0][1] = null;
        rows[0][2] = null;
        rows[0][3] = null;
        rows[0][4] = null;
        rows[0][5] = null;

        File fileName = createTemporaryFile();
        DBFWriter writer = DBASEFactory.dbf3().getDBFWriter(fileName);
        writer.writeRows(fields,rows);
        System.out.println(fileName);
        DBFReader<Object[]> reader = DBASEFactory.dbf3().getDBFRawReader(fileName.getAbsolutePath());
        List<Object[]> readRows = reader.readAllRows();
        assertEquals(1,readRows.size());
        Object[] row = readRows.get(0);
        assertNull(row[0]);
        assertEquals(0.0f,(float)row[1],0.0000001);
        assertEquals(0,(int)row[2]);
        assertEquals(0.0,(double)row[3],0.000000001);
        assertNull(row[4]);
        assertFalse((boolean) row[5]);
        reader.close();
    }


    private boolean isContainsRow(Object[] row, List<Object[]> readRows) {
        t:for (Object[] readRow:readRows){
            if (row.length != readRow.length) continue;
            for (int i=0;i<row.length;i++){
                if (row[i] instanceof Date && !(readRow[i] instanceof Date)) continue t;
                if (!(row[i] instanceof Date) && readRow[i] instanceof Date) continue t;
                if (row[i] instanceof Date && readRow[i] instanceof Date){
                    if (!compareDate((Date)row[i],(Date)readRow[i])) continue t;
                    else continue ;
                }
                if (!row[i].equals(readRow[i])) continue t;
            }
            return true;
        }
        return false;
    }

    private boolean compareDate(Date date,Date thatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date).equalsIgnoreCase(sdf.format(thatDate));
    }

    public static File createTemporaryFile() {
        File fileName;
        try {
            fileName = File.createTempFile("DBF-",".tmp");
            fileName.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }
}
