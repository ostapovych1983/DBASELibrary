package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.log;

class EmptyDBFLogger implements DBFLogger {
    @Override
    public void fatal(String messages, Object... params) {
        //NOP
    }

    @Override
    public void error(String messages, Object... params) {
        //NOP
    }

    @Override
    public void warning(String messages, Object... params) {
        //NOP
    }

    @Override
    public void info(String messages, Object... params) {
        //NOP
    }

    @Override
    public void debug(String messages, Object... params) {
        //NOP
    }

    @Override
    public void trace(String messages, Object... params) {
        //NOP
    }
}
