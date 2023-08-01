package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;

public interface DBFLogger {
    void fatal(String messages,Object ... params);
    void error(String messages,Object ... params);
    void warning(String messages,Object ... params);
    void info(String messages,Object ... params);
    void debug(String messages,Object ... params);
    void trace(String messages,Object ... params);

}
