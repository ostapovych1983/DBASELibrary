package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbfoptions;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;

@SuppressWarnings("ALL")
public class DBFOptions {
    private boolean readDeleted = true;
    private boolean readCodePageFromTable = false;

    private boolean validateAnotation = false;

    private DBFCodePage codePage;

    private boolean enebleLog;

    public static DBFOptions defaultOptions() {
        DBFOptions options = new DBFOptions();
        options.setCodePage(DBFCodePage.RUSSIAN_WINDOWS);
        options.setEnebleLog(true);
        return options;
    }

    public boolean isReadDeleted() {
        return readDeleted;
    }

    public void setReadDeleted(boolean readDeleted) {
        this.readDeleted = readDeleted;
    }

    public DBFCodePage getCodePage() {
        return codePage;
    }

    public void setCodePage(DBFCodePage codePage) {
        this.codePage = codePage;
    }

    public boolean isReadCodePageFromTable() {
        return readCodePageFromTable;
    }

    public void setReadCodePageFromTable(boolean readCodePageFromTable) {
        this.readCodePageFromTable = readCodePageFromTable;
    }

    public boolean isValidateAnotation() {
        return validateAnotation;
    }

    public void setValidateAnotation(boolean validateAnotation) {
        this.validateAnotation = validateAnotation;
    }

    public boolean isEnebleLog() {
        return enebleLog;
    }

    public void setEnebleLog(boolean enebleLog) {
        this.enebleLog = enebleLog;
    }

    @Override
    public String toString() {
        return "DBFOptions{" +
                "readDeleted=" + readDeleted +
                ", readCodePageFromTable=" + readCodePageFromTable +
                ", validateAnotation=" + validateAnotation +
                ", enebleLog=" + enebleLog +
                ", codePage=" + codePage +
                '}';
    }
}
