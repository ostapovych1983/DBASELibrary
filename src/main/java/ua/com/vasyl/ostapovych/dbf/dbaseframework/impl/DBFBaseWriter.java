package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFFile;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.Utils;
import java.io.File;
import java.util.List;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.FileUtils.createFile;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.FileUtils.rewriteExistFile;

public class DBFBaseWriter implements DBFWriter {
    private final File file;
    private final Utils utils = new Utils();

    public DBFBaseWriter(File file) {
        this.file = file;
    }

    @Override
    public <T> void writeRows(List<T> rows,Class<T> tClass) {
        rewriteExistFile(file);
        DBFFile dbfFile = null;
        try {
            dbfFile = createFile(file);
            _writeRows(rows, tClass,dbfFile);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (dbfFile != null) dbfFile.close();
        }
    }

    @Override
    public void writeRows(DBFField[] fields, Object[][] rows) {
        DBFFile dbfFile = null;
        try {
            rewriteExistFile(file);
            dbfFile = createFile(file);
            _writeRows(fields, rows,dbfFile);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (dbfFile != null) dbfFile.close();
        }
    }

    private <T> void _writeRows(List<T> rows, Class<T> tClass,DBFFile file) {
        if (rows == null) throw new DBFIllegalValueException("DBF Rows cannot be null");
        DBFGenerateStrategies strategy = utils.checkStrategy(tClass);
        DBFField [] fields = utils.generateFields(tClass,strategy);
        byte [] caption = utils.createCaption(fields,rows.size());
        file.write(caption);
        for (T row:rows){
            byte[] rowAsByte = utils.toDBFRecord(fields,row);
            file.write(rowAsByte);
        }
        file.write(0x1A);
    }
    private void _writeRows(DBFField[] fields, Object[][] rows, DBFFile file) {
        if (rows == null) throw new DBFIllegalValueException("DBF Rows cannot be null");
        byte [] caption = utils.createCaption(fields,rows.length);
        file.write(caption);
        for (Object[] row:rows){
            byte[] rowAsByte = utils.toDBFRecord(fields, row);
            file.write(rowAsByte);
        }
        file.write(0x1A);
    }
}
