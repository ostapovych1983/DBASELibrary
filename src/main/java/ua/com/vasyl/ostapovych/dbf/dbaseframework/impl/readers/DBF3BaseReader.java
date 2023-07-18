package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.readers;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.DBFRow;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;


class DBF3BaseReader extends DBFReaderAbstract<DBFRow> {
    DBF3BaseReader(String fileName, DBFOptions options) {
        super(fileName, options);
    }

    @Override
    protected DBFRow convert(DBFRow row) {
        return row;
    }

}
