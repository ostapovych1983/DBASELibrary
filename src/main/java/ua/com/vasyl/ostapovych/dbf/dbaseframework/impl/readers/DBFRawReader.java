package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;


class DBFRawReader extends DBFReaderAbstract<Object[]> {

    DBFRawReader(String fileName, DBFOptions options) {
        super(fileName, options);
    }

    @Override
    protected Object[] convert(DBFRow row) {
        return row.toRawRow();
    }
}
