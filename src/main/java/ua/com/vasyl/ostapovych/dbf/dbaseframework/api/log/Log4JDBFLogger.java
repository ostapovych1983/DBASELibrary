package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;

import java.lang.reflect.Method;

public class Log4JDBFLogger implements DBFLogger {
    private final Object logger;


    public Log4JDBFLogger() {
        logger = getLog();
    }

    @Override
    public void fatal(String messages, Object ... params) {
        invoke(logger, "error");
    }

    @Override
    public void error(String messages, Object ... params) {
        invoke (logger,"error");
    }

    @Override
    public void warning(String messages, Object... params) {
        invoke(logger,"warn");
    }

    @Override
    public void info(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isInfoEnabled");
        if (isEnabled) {
            invoke(logger,"info");
        }
    }

    @Override
    public void debug(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isDebugEnabled");
        if (isEnabled) {
            invoke(logger,"debug");
        }
    }

    @Override
    public void trace(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isTraceEnabled");
        if (isEnabled) {
            invoke(logger,"trace");
        }
    }

    private Object getLog() {
        try {
            Class<?> logFactoryClass = Class.forName("org.apache.log4j.Logger");
            Method method = logFactoryClass.getMethod("getLogger", String.class);
            return method.invoke(null,"Log4JDBFLogger");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invoke(Object logger,String methodName){
        try {
            Method method = logger.getClass().getMethod(methodName,Object.class);
            method.setAccessible(true);
            method.invoke(logger, (Object) null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private boolean invokeBoolean(Object logger,String methodName,Object ... params){
        try {
            Method method = logger.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (boolean) method.invoke(logger,params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
