package ua.com.vasyl.ostapovych.dbf.dbaseframework;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLogger;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers.DBFReaderFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers.DBFWriterFactory;

import java.io.File;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLoggerManager.getDBFLogger;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLoggerManager.getEmptyDBFLogger;


public class DBASEFactory {

    public static DBF3 dbf3(){
        DBFOptions options = new DBFOptions();
        options.setCodePage(DBFCodePage.NONE);
        options.setReadDeleted(true);
        options.setValidateAnotation(true);
        return dbf3(options);
    }
    public static DBF3 dbf3(DBFOptions options){
        final DBFLogger logger;
        if (options.isEnebleLog()) {
            logger = getDBFLogger("DBFLibraryLogger");
        }
        else {
            logger = getEmptyDBFLogger();
        }
        return new DBF3(options,logger);
    }

    public static class DBF3{
        private final DBFOptions options;
        private final DBFLogger logger;

        public DBF3(DBFOptions options, DBFLogger logger) {
            this.options = options;
            this.logger = logger;
        }

        public DBFReader<DBFRow> getDBFReader(String dbfFileName) {
            return DBFReaderFactory.getDBFReader(dbfFileName,options,logger);
        }

        public DBFReader<Object[]> getDBFRawReader(String dbfFileName) {
            return DBFReaderFactory.getDBFRawReader(dbfFileName,options,logger);
        }

        public <T> DBFReader<T> getCustomDBFReader(String fileName, Class<T> type) {
            return DBFReaderFactory.getCustomDBFReader(fileName,type,options,logger);
        }

        public DBFWriter getDBFWriter(File filePath) {
            return DBFWriterFactory.getDBFWriter(filePath);
        }
    }
}
