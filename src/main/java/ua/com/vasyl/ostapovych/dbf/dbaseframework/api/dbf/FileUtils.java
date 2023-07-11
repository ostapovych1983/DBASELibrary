package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf;

import java.io.File;

public class FileUtils {
    public static DBFFile createFile(File file){
        return new DBFFile(file);
    }
}
