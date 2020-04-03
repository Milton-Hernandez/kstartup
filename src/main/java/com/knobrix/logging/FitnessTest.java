package com.knobrix.logging;

import com.knobrix.config.AppInfo;
import com.knobrix.config.Settings;
import com.knobrix.logging.Log.LogLevel;

public class FitnessTest {
    public static void main(String args[]) {
        try {            
            //Settings.init();  
            Log.ToConsole = true;
            Log.Level = LogLevel.ALERT;
            //Log.reset();
            Log.debug("App Home Folder: " + AppInfo.One.getAppHome());
            Log.info("App Name: " + AppInfo.One.getAppName());
            Log.error("App Platform: " + AppInfo.One.Platform);
            Log.warn("Logging to File: " + Log.ToFile);
            for(int i=0; i<100; i++)
               Log.trace("Log File Name: ", Log.getLogFileName());
            
            Log.error("================================================");
            
            Log.Level = LogLevel.TRACE;
            Log.reset();
            Log.debug("App Home Folder: " + AppInfo.One.getAppHome());
            Log.info("App Name: " + AppInfo.One.getAppName());
            Log.error("App Platform: " + AppInfo.One.Platform);
            Log.get().warn("Logging to File: " + Log.ToFile);
            for(int i=0; i<10; i++)
               Log.get().trace("Log File Name: ", Log.getLogFileName());
            
            throw new IllegalArgumentException("Fake Exception");
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
