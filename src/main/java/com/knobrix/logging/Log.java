package com.knobrix.logging;

import com.knobrix.config.AppInfo;
import com.knobrix.config.Configurable;
import com.knobrix.config.Settings;


import java.io.File;

public class Log extends Configurable {
    
    private static Object lock = new Object();
    
    public enum LogLevel {TRACE,DEBUG,INFOR,ALERT,ERROR}
    public static Boolean ToFile = false;
    public static Boolean ToConsole = false;
    public static LogLevel Level = LogLevel.TRACE;
    public static Integer MaxLogSize = 25 * 1024 * 1024;
    public static Integer MaxLogCount = 10;
      
    public static Boolean dirty = false;
    
    
    protected static LogInterface One;
    
    public static LogInterface get() {
        if(One == null || dirty) {
            synchronized(lock) {
              One = new BasicLog();
              dirty = false;
           }
        }
        return One;
    }
    
    public static void reset() {
       dirty = true;    
    }   
    
    
    public static void trace(String msg, Object ... args)
    {
        get().trace(msg, args);
    }
    public static  void debug(String msg, Object ... args)
    {
        get().debug(msg, args);
    }
    public static  void info(String msg, Object ... args)
{
        get().info(msg, args);
    }            
    public static  void warn(String msg, Object ... args)
   {
        get().warn(msg, args);
    } 
    public static  void error(String msg, Object ... args)
    {
        get().error(msg, args);
    }
    public static  void error(Throwable ex)
    {
        get().error(ex);
    }
    public static  void warn(Throwable ex)
    {
        get().warn(ex);
    }
    
    public static String getLogFileName() {
        var folder = AppInfo.One.getAppHome();
        var appName = AppInfo.One.getAppName();
        var instance = AppInfo.One.getInstance();
        var fname = folder + File.separator + "logs";
        var dir = new File(fname);
        if(!dir.exists() || !dir.isDirectory())
            dir.mkdir();
        fname += File.separator + instance + "-" + appName + ".%u.%g.log";
        return fname;
    }
    
    
   
   
}
