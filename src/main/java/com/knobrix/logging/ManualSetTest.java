package com.knobrix.logging;

import com.knobrix.config.AppInfo;
import com.knobrix.config.Settings;


public class ManualSetTest {
    public static void main(String args[]) {
        try {
            Log.ToConsole = true;
            Log.Level = Log.LogLevel.DEBUG;
            Log.get().debug("App Home Folder: " + AppInfo.One.getAppHome());
            Log.get().info("App Name: " + AppInfo.One.getAppName());
            Log.get().error("App Platform: " + AppInfo.One.Platform);
            Log.get().warn("Logging to File: " + Log.ToFile);
            Log.get().trace("Log File Name: ", Log.getLogFileName());
            throw new IllegalArgumentException("Fake Exception");
        }
        catch(Throwable t) {
            Log.get().error(t);
        }
    }
}
