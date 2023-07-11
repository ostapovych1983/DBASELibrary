package ua.com.vasyl.ostapovych.dbf.dbaseframework.api;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions.DBFOptions;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.interfaces.DBF3;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.DBF3Impl;
public class DBASEFactory {
    public static DBF3 dbf3(){
        DBFOptions options = new DBFOptions();
        options.setCodePage(DBFCodePage.NONE);
        options.setReadDeleted(true);
        return new DBF3Impl(options);
    }
    public static DBF3 dbf3(DBFOptions options){
        return new DBF3Impl(options);
    }
}
