package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFileOpenException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFWriteException;

import java.io.*;

public class DBFFile implements Closeable {

    private final FileOutputStream fileOutputStream;
    public DBFFile(File file){
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new DBFFileOpenException(e);
        }
    }

    public void write(int b)  {
        try {
            fileOutputStream.write(b);
        } catch (IOException e) {
            close();
            throw new DBFWriteException(e);
        }
    }

    public void write(byte[] b){
        try {
            fileOutputStream.write(b);
        } catch (IOException e) {
            close();
            throw new DBFWriteException(e);
        }
    }


    @Override
    public void close(){
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
