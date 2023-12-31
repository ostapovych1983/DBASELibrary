package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFFile;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFWriteException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLogger;

import java.io.File;
import java.util.List;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.FileUtils.createFile;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils.checkStrategy;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers.WriteUtils.*;


class DBFBaseWriter implements DBFWriter {
    private final File file;
    private final DBFLogger logger;

    public DBFBaseWriter(File file, DBFLogger logger) {
        this.logger = logger;
        this.file = file;
    }

    @Override
    public <T> void writeRows(List<T> rows,Class<T> tClass) {
        deleteFileIfExist(file);
        try (DBFFile dbfFile = createFile(file)) {
            writeRowsToFile(rows, tClass, dbfFile);
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(e);
            logger.error("Error write rows .Error = '{}'",e.getMessage());
            throw exception;
        }
    }

    @Override
    public void writeRows(DBFField[] fields, Object[][] rows) {
        DBFFile dbfFile = null;
        try {
            deleteFile(file);
            dbfFile = createFile(file);
            writeRowsToFile(logger,fields, rows,dbfFile);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (dbfFile != null) dbfFile.close();
        }
    }

    private <T> void writeRowsToFile(List<T> rows, Class<T> tClass, DBFFile file) {
        if (rows == null) throw new DBFIllegalValueException("DBF Rows cannot be null");
        DBFGenerateStrategies strategy = checkStrategy(tClass);
        DBFField [] fields = generateFields(tClass,strategy);
        byte [] caption = createCaption(fields,rows.size());
        file.write(caption);
        for (T row:rows){
            byte[] rowAsByte = toDBFRecord(logger,fields,row);
            file.write(rowAsByte);
        }
        file.write(0x1A);
    }
    private void writeRowsToFile(DBFLogger logger,DBFField[] fields, Object[][] rows, DBFFile file) {
        if (rows == null) throw new DBFIllegalValueException("DBF Rows cannot be null");
        byte [] caption = createCaption(fields,rows.length);
        file.write(caption);
        for (Object[] row:rows){
            byte[] rowAsByte = toDBFRecord(logger,fields, row);
            file.write(rowAsByte);
        }
        file.write(0x1A);
    }

    private static void deleteFileIfExist(File file) {
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
