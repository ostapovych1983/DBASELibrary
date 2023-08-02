package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;



import java.lang.reflect.Method;

public class CommonLoggingDBFLogger implements DBFLogger {

    private final Object logger;

    public CommonLoggingDBFLogger() {
        logger = getLog();
    }

    @Override
    public void fatal(String messages, Object ... params) {
        boolean isFatalEnabled = invokeBoolean(logger,"isFatalEnabled");
        if (isFatalEnabled) {
            invoke(logger, "fatal", messages);
        }
    }

    @Override
    public void error(String messages, Object ... params) {
        boolean isFatalEnabled = invokeBoolean(logger,"isErrorEnabled");
        if (isFatalEnabled) {
            invoke (logger,"error",messages);
        }
    }

    @Override
    public void warning(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isWarnEnabled");
        if (isEnabled) {
            invoke(logger,"warn",messages);
        }
    }

    @Override
    public void info(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isInfoEnabled");
        if (isEnabled) {
            invoke(logger,"info",messages);
        }
    }

    @Override
    public void debug(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isDebugEnabled");
        if (isEnabled) {
            invoke(logger,"debug",messages);
        }
    }

    @Override
    public void trace(String messages, Object... params) {
        boolean isEnabled = invokeBoolean(logger,"isTraceEnabled");
        if (isEnabled) {
            invoke(logger,"trace",messages);
        }
    }

    private Object getLog() {
        try {
            Class<?> logFactoryClass = Class.forName("org.apache.commons.logging.LogFactory");
            Method method = logFactoryClass.getMethod("getLog", String.class);
            return method.invoke(null,"DBFLogger");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invoke(Object logger,String methodName,Object ... params){
        try {
            Method method = logger.getClass().getMethod(methodName,Object.class);
            method.setAccessible(true);
            method.invoke(logger,params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private boolean invokeBoolean(Object logger,String methodName,Object ... params){
        try {
            Method method = logger.getClass().getMethod(methodName,params.getClass());
            method.setAccessible(true);
            return (boolean) method.invoke(logger,params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
