package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFWriteException;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void rewriteExistFile(File outputFile) {
        if (outputFile.exists()){
            boolean isSuccessfulDeleted = outputFile.delete();
            if (!isSuccessfulDeleted) {
                throw new DBFWriteException(String.format("Cannot delete existed dbf file %s", outputFile.getName()));
            }
        }
    }
    public static File createTemporaryFile() {
        File fileName;
        try {
            fileName = File.createTempFile("DBF-",".tmp");
            fileName.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }
}
