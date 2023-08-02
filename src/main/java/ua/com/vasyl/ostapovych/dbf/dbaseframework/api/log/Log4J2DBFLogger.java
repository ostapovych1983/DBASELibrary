package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;

import java.lang.reflect.Method;

public class Log4J2DBFLogger implements DBFLogger {
    private final Object logger;

    public Log4J2DBFLogger() {
        logger = getLog();
    }

    @Override
    public void fatal(String messages, Object ... params) {
        boolean isFatalEnabled = invokeBoolean(logger,"isFatalEnabled");
        if (isFatalEnabled) {
            invoke(logger, "error", messages,params);
        }
    }

    @Override
    public void error(String messages, Object ... params) {
        boolean isFatalEnabled = invokeBoolean(logger,"isErrorEnabled");
        if (isFatalEnabled) {
            invoke (logger,"error",messages,params);
        }
    }

    @Override
    public void warning(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isWarnEnabled");
        if (isEnabled) {
            invoke(logger,"warn",messages,params);
        }
    }

    @Override
    public void info(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isInfoEnabled");
        if (isEnabled) {
            invoke(logger,"info",messages,params);
        }
    }

    @Override
    public void debug(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isDebugEnabled");
        if (isEnabled) {
            invoke(logger,"debug",messages,params);
        }
    }

    @Override
    public void trace(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isTraceEnabled");
        if (isEnabled) {
            invoke(logger,"trace",messages,params);
        }
    }

    private Object getLog() {
        try {
            Class<?> logFactoryClass = Class.forName("org.apache.logging.log4j.LogManager");
            Method method = logFactoryClass.getMethod("getLogger", String.class);
            return method.invoke(null,"Log4J2DBFLogger");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invoke(Object logger,String methodName,Object ... params){
        try {
            Method method = logger.getClass().getMethod(methodName,String.class,Object[].class);
            method.setAccessible(true);
            method.invoke(logger,params);
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
