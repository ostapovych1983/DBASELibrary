package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFWriteException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFFile;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.io.File;
import java.util.List;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.FileUtils.createFile;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.Utils.*;

public class DBFBaseWriter implements DBFWriter {
    private final File file;

    public DBFBaseWriter(File file) {
        this.file = file;
    }

    @Override
    public <T> void writeRows(List<T> rows,Class<T> tClass) {
        rewriteExistFile(file);
        try (DBFFile dbfFile = createFile(file)) {
            _writeRows(rows, tClass, dbfFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeRows(DBFField[] fields, Object[][] rows) {
        DBFFile dbfFile = null;
        try {
            deleteFile(file);
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
        DBFGenerateStrategies strategy = checkStrategy(tClass);
        DBFField [] fields = generateFields(tClass,strategy);
        byte [] caption = createCaption(fields,rows.size());
        file.write(caption);
        for (T row:rows){
            byte[] rowAsByte = toDBFRecord(fields,row);
            file.write(rowAsByte);
        }
        file.write(0x1A);
    }
    private void _writeRows(DBFField[] fields, Object[][] rows, DBFFile file) {
        if (rows == null) throw new DBFIllegalValueException("DBF Rows cannot be null");
        byte [] caption = createCaption(fields,rows.length);
        file.write(caption);
        for (Object[] row:rows){
            byte[] rowAsByte = toDBFRecord(fields, row);
            file.write(rowAsByte);
        }
        file.write(0x1A);
    }

    private static void rewriteExistFile(File file) {
        if (file.exists()){
            boolean isSuccessfulDeleted = file.delete();
            if (!isSuccessfulDeleted) {
                throw new DBFWriteException(String.format("Cannot delete existed dbf file %s", file.getName()));
            }
        }
    }

    private static void deleteFile(File file) {
        if (file.exists()){
            boolean isSuccessfulDeleted = file.delete();
            if (!isSuccessfulDeleted) {
                throw new DBFWriteException(String.format("Cannot delete existed dbf file %s", file.getName()));
            }
        }
    }
}
