package ua.com.vasyl.ostapovych.dbf.dbaseframework.api;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBF3;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFWriter;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers.DBFReaderFactory;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.writers.DBFWriterFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DBASEFactory {

    private static boolean isunitLog = false;
    private static final Logger logger = Logger.getLogger(DBASEFactory.class.getName());

    public static DBF3 dbf3(){
        DBFOptions options = new DBFOptions();
        options.setCodePage(DBFCodePage.NONE);
        options.setReadDeleted(true);
        options.setValidateAnotation(true);
        return dbf3(options);
    }
    public static DBF3 dbf3(DBFOptions options){
        initLog(options.isEnableLog());
        logger.log(Level.CONFIG,String.format("Getting dbf3 reader and writer factories with parameters %s",options));
        return new DBF3() {
            @Override
            public DBFReader<DBFRow> getDBFReader(String dbfFileName) {
                return DBFReaderFactory.getDBFReader(dbfFileName,options);
            }

            @Override
            public DBFReader<Object[]> getDBFRawReader(String dbfFileName) {
                return DBFReaderFactory.getDBFRawReader(dbfFileName,options);
            }

            @Override
            public <T> DBFReader<T> getCustomDBFReader(String fileName, Class<T> type) {
                return DBFReaderFactory.getCustomDBFReader(fileName,type,options);
            }

            @Override
            public DBFWriter getDBFWriter(File filePath) {
                return DBFWriterFactory.getDBFWriter(filePath);
            }
        };
    }

    private static void initLog(boolean enableLog) {
        if (enableLog && !isunitLog){
            try {
                LogManager.getLogManager()
                        .readConfiguration(
                                DBASEFactory.class.getClassLoader()
                                        .getResourceAsStream("DbfLogConfiguration.properties")
                        );
            } catch (SecurityException | IOException e1) {
                e1.printStackTrace();
            }
            isunitLog = true;
        }
    }
}
