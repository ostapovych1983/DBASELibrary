package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

class JULDBFLogger implements DBFLogger {

    private final Map<String,Logger> loggers;

    private final String loggerName;
    private boolean isInitLog = false;

    public JULDBFLogger(String loggerName) {
        loggers = new HashMap<>();
        this.loggerName = loggerName;
    }

    @Override
    public void fatal(String messages, Object ... params) {
        getLogger(loggerName).log(Level.SEVERE,messages,params);
    }

    @Override
    public void error(String messages, Object... params) {
        getLogger(loggerName).log(Level.WARNING,messages,params);
    }

    @Override
    public void warning(String messages, Object... params) {
        getLogger(loggerName).log(Level.WARNING,messages,params);
    }

    @Override
    public void info(String messages, Object... params) {
        getLogger(loggerName).log(Level.INFO,messages,params);
    }

    @Override
    public void debug(String messages, Object... params) {
        getLogger(loggerName).log(Level.CONFIG,messages,params);
    }

    @Override
    public void trace(String messages, Object... params) {
        getLogger(loggerName).log(Level.FINE,messages,params);
    }

    private Logger getLogger(String loggerName) {
        LogManager logManager  = null;
        if (!isInitLog){
            try {
                logManager = LogManager.getLogManager();
                logManager.readConfiguration();
            } catch (SecurityException | IOException e) {
                throw new RuntimeException(e);
            }
            isInitLog = true;
        }
        if (!loggers.containsKey(loggerName)) {
            Logger logger = Logger.getLogger(loggerName);
            requireNonNull(logManager).addLogger(logger);
            loggers.put(loggerName, logManager.getLogger(loggerName));
        }
        return loggers.get(loggerName);
    }

}
