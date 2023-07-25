package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.readerstest.utils.UtilsTests.getDBFTableByResourceName;

public class Instruments {
    public static List<String[]> getExampleRows(){
        String examplePath = getDBFTableByResourceName("dbf/dbf3/examples.cvs");
        return generateExampleRows(examplePath);
    }

    public static List<String[]> getExampleRowsWithDeleted() {
        String examplePath = getDBFTableByResourceName("dbf/dbf3/examplesWithDeletedRow.cvs");
        return generateExampleRows(examplePath);
    }

    private static List<String[]> generateExampleRows(String examplePath) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(examplePath));
            String line;
            List<String[]> res = new ArrayList<>();
            while (true){
                line =  bufferedReader.readLine();
                if (line == null) break;
                res.add(line.split(";"));
            }
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<String[]> getExampleRowsWithDeletedEOF() {
        String examplePath = getDBFTableByResourceName("dbf/dbf3/examplesWithDeletedRowEOF.cvs");
        return generateExampleRows(examplePath);
    }

    public static List<String[]> getExampleRowsDosCodePage() {
        String examplePath = getDBFTableByResourceName("dbf/dbf3/examplesWithDosCodePage.cvs");
        return generateExampleRows(examplePath);
    }
}
