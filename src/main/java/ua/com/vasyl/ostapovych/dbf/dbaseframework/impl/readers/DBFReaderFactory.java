package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFGenerateStrategies;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBFReader;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log.DBFLogger;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers.ReadUtils.*;
import static ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils.DBFUtils.checkStrategy;

public class DBFReaderFactory {

    public static DBFReader<DBFRow> getDBFReader(String dbfFileName, DBFOptions options, DBFLogger logger) {
        return new DBFReaderAbstract<DBFRow>(dbfFileName,options,logger) {
            @Override
            protected DBFRow convert(DBFRow row) {
                return row;
            }
        };
    }

    public static DBFReader<Object[]> getDBFRawReader(String dbfFileName, DBFOptions options,DBFLogger logger) {
        return new DBFReaderAbstract<Object[]>(dbfFileName,options,logger) {
            @Override
            protected Object[] convert(DBFRow row) {
                return row.toRawRow();
            }
        };
    }

    public static <T> DBFReader<T> getCustomDBFReader(String dbfFileName,Class<T> type, DBFOptions options,DBFLogger logger) {
        return new DBFReaderAbstract<T>(dbfFileName, options, logger) {
            @Override
            protected T convert(DBFRow row) {
                T res = createInstance(type);
                DBFGenerateStrategies strategies = checkStrategy(type);
                switch(strategies){
                    case AUTOMATIC: return fillByAutomaticStrategy(res,row);
                    case ANNOTATED_FIELDS: return fillByAnnotatedFieldStrategy(res,row);
                    case ANNOTATED_GETTERS: return fillByAnnotatedGetterFieldStrategy(res,row);
                    case FIELDS_NAME: return fillByFieldNameStrategy(res,row);
                    case GETTERS_NAME: return fillByGetterNameStrategy(res,row);
                }
                return res;
            }
        };
    }

}
