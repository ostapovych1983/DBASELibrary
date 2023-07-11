package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types;


public enum DBFType {
    CHARACTER('C'),DATE('D'),FLOAT('F'),
    NUMERIC('N'),UNKNOWN('-'),MEMO('M'),
    LOGICAL('L');
    private final char symbol;


    DBFType(char symbol) {

        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
