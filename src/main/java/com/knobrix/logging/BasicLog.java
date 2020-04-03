package com.knobrix.logging;

import java.io.IOException;
import java.util.logging.*;


import static java.util.logging.Level.FINEST;

public class BasicLog implements LogInterface {
    //<editor-fold desc="Nutty bits...">
    private final Logger _innerLogger;
    private void ConfigConsoleLog() {
        var consFormat = new ConsoleFormatter();

        for (var h : _innerLogger.getHandlers())
            h.setFormatter(consFormat);
        var cons = new ConsoleHandler();
        cons.setFormatter(consFormat);
        cons.setLevel(convert(Log.Level));
        _innerLogger.addHandler(cons);
    }
    private void ConfigFileLog() {
        try {
            var fname = Log.getLogFileName();
            var fhandler = new FileHandler(fname,
                    Log.MaxLogSize, Log.MaxLogCount, true);
            fhandler.setLevel(convert(Log.Level));
            fhandler.setFormatter(new FileFormatter());
            _innerLogger.addHandler(fhandler);
        } catch (IOException ex) {
            throw new Error("LOG_CONFIG: Could not initialize Log: ");
        }
    }

    private static java.util.logging.Level convert(Log.LogLevel arg) {
       switch(arg) {
           case TRACE:
               return FINEST;
           case DEBUG:
               return java.util.logging.Level.FINE;
           case INFOR:
               return java.util.logging.Level.INFO;
           case ALERT:
               return java.util.logging.Level.WARNING;
           case ERROR:
               return java.util.logging.Level.SEVERE;
           default:
               return java.util.logging.Level.SEVERE;
       }
    }
    private static Log.LogLevel convert(java.util.logging.Level arg) {
        if(arg == java.util.logging.Level.FINEST)
                return Log.LogLevel.TRACE;
        else if(arg == java.util.logging.Level.FINER)
                return Log.LogLevel.TRACE;
        else if(arg == java.util.logging.Level.FINE)
                return Log.LogLevel.DEBUG;
        else if(arg == java.util.logging.Level.INFO)
                return Log.LogLevel.INFOR;
        else if(arg == java.util.logging.Level.WARNING)
                return Log.LogLevel.INFOR;
        else if(arg == java.util.logging.Level.SEVERE)
                return Log.LogLevel.ERROR;
        else
                return Log.LogLevel.ERROR;
    }

    protected BasicLog() {
        _innerLogger = Logger.getLogger("Log");
        _innerLogger.setUseParentHandlers(false);
        _innerLogger.setLevel(convert(Log.Level));
        
        var h = _innerLogger.getHandlers();
        
        for(int i=0; i<h.length; i++)
             _innerLogger.removeHandler(h[i]);
        
        if(Log.ToConsole)
            ConfigConsoleLog();

        if(Log.ToFile)
            ConfigFileLog();        
    }
    
  
    
    //</editor-fold>
    @Override
    public void error(String msg, Object ... args) {
        _innerLogger.log(java.util.logging.Level.SEVERE, msg, args);
    }
    @Override
    public  void error(Throwable t) {
        _innerLogger.log(java.util.logging.Level.SEVERE, t.getMessage(), t.getStackTrace());
    }
    @Override
    public  void warn(Throwable t) {
        _innerLogger.log(java.util.logging.Level.WARNING, t.getMessage(), t.getStackTrace());
    }
    @Override
    public void warn(String msg, Object ... args) {
        _innerLogger.log(java.util.logging.Level.WARNING, msg, args);
    }
    @Override
    public void info(String msg, Object ... args) {
        _innerLogger.log(java.util.logging.Level.INFO, msg, args);
    }
    @Override
    public void debug(String msg, Object ... args) {
        _innerLogger.log(java.util.logging.Level.FINE,msg, args);
    }
    @Override
    public  void trace(String msg, Object ... args) {
        _innerLogger.log(FINEST, msg, args);
    }

    //Helper Classes
    class FileFormatter extends Formatter {
        private final String delim = " | ";

        @Override
        public String format(LogRecord record) {
            var buff = new StringBuffer();
            buff.append((new java.util.Date(record.getMillis())).toInstant().toString()).append(delim);
            buff.append(convert(record.getLevel())).append(delim);
            buff.append(record.getMessage());
            for(var par : record.getParameters())
                buff.append(delim).append(par.toString());
            buff.append('\n');
            return buff.toString();
        }
    }
    class ConsoleFormatter extends Formatter {
        private final String delim = " | ";

        @Override
        public String format(LogRecord record) {
            var buff = new StringBuffer();
            buff.append((new java.util.Date(record.getMillis())).toInstant().toString()).append(delim);
            buff.append(convert(record.getLevel())).append(delim);
            buff.append(record.getMessage());

            for(var par : record.getParameters())
                buff.append(delim).append(par.toString());
            buff.append('\n');
            return buff.toString();
        }
    }
}
