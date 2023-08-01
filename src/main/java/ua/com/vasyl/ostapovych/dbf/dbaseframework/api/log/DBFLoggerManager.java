package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;

public class DBFLoggerManager {

    public static DBFLogger getDBFLogger(String loggerName){
         return getLoggerByAutomaticallyLoggerChoose(loggerName);
    }

    private static DBFLogger getLoggerByAutomaticallyLoggerChoose(String loggerName) {
        if (presentClass("org.slf4j.LoggerFactory") && presentClass("ch.qos.logback.classic.Logger")){
            return new LogbackDBFLogger();
        }
        if (presentClass("org.slf4j.LoggerFactory")){
            return new Slf4JDBFLogger();
        }
        if (presentClass("org.apache.commons.logging.Log") && presentClass("org.apache.commons.logging.LogFactory")){
            return new CommonLoggingDBFLogger();
        }
        if (presentClass("org.apache.logging.log4j.LogManager") && presentClass("org.apache.logging.log4j.Logger")){
            return new Log4J2DBFLogger();
        }
        if (presentClass("org.apache.log4j.Logger")){
            return new Log4JDBFLogger();
        }
        return new JULDBFLogger(loggerName);
    }

    private static boolean presentClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static DBFLogger getEmptyDBFLogger() {
        return new EmptyDBFLogger();
    }
}
