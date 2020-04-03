package com.knobrix.config;
import java.io.File;

/**
 * Basic container for App settings.  Implements Singleton Pattern.
 * To Access functionality, use the 'One' Instance variable
 * @author Milton Hernandez
 * @copyright Epheriant, LLC, 2020.
 */
public class AppInfo {
    /**enum representing Platform: Windows, Linux, Android, MacOS, IOS */
    public enum OS { Windows,Linux,MacOS,Android,IOS,Other }
    /**Safe Singleton*/
    public final static AppInfo One = new AppInfo();

    //<editor-fold desc="Nutty bits...">
    private AppInfo() {
        resetAppName();
    }
    private String AppName;
    private boolean _recklessMode = false;
    private OS getPlatform() {
        var os = System.getProperty("os.name").toLowerCase();
        if(os.toLowerCase().contains("win"))
            return OS.Windows;
        else if(os.toLowerCase().contains("lin"))
            return OS.Linux;
        else if(os.toLowerCase().contains("mac"))
            return OS.MacOS;
        //Settings for Android and IOS pending
        else
            return OS.Other;
    }
    private LockHandler locks;
    private String defaultAppName()  {
        try {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String AppName = Class.forName(stack[stack.length - 1].getClassName()).getPackageName();
            AppName = AppName.replace('.', '_').toLowerCase();
            return AppName;
        }
        catch(Exception ex) {
            throw new Error("Couldn't load class from name");
        }
    }
    //</editor-fold>

    /**
     * Sets the configuration to try to ignore errors and not Panic
     * Errors.
     */
    public void recklessModeOn() {
        _recklessMode = true;
    }

    /**
     * Returns a unique sequential int for each additional app entity
     * that is running under the same AppName.
     */
    public int getInstance() {
        return locks.getInstance();
    }

    /**
     * Sets the configuration to Panic when there are errors
     */
    public void recklessModeOff() {
        _recklessMode = false;
    }

    /**
     * Holds the Major OS Platform the application is operating on.
     * One of OS { Windows,Linux,MacOS,Android,IOS,Other }
     */
    public final OS Platform = getPlatform();

    /**
     * Resets the App Name to Default.  The default name is
     * the full package name of the main class with the periods replaced with '-' and
     * turned to lower case.  For example,  if the main class is 'com.knobrix.Main.Launcher',
     * the default app name will be 'com_knobrix_main'.
     */
    public void resetAppName() {
        AppName = defaultAppName();
        if(locks != null)
            locks.release();
        locks = new LockHandler(getAppHome());
    }

    /**
     * Sets the AppName to the given argument. Beware that this also affects the default home folder.
     * @param  argAppName : New Name for the Application
     */
    public void setAppName(String argAppName) {
        AppName = argAppName;
        if(locks != null)
            locks.release();
        locks = new LockHandler(getAppHome());
    }

    /**
     * @return Name of the App
     */
    public String getAppName() {
        return AppName;
    }

    /**
     * @return Home folder for the Application. The logic for the Home folder is as follows:
     * 1) First, it would check if an envirnoment variable called {AppName.toUpperCase()}_HOME exists.  if it
     *      does, then the AppHome will be the value set in that variable.
     * 2) If the environment variable does not exist, and the OS.Platform is Windows, it will look for the
     *      environment variable {%USERPROFILE%}/.{AppName}.
     * 3) If the environment variable does not exist, and the OS.Platform is NOT Windows, it will look for the
     *      environment variable {$HOME}/.{AppName}.
     * 4) Otherwise, it will just return the current executing directory directory
     * @throws Error if home folder doesn't exist and it cannot be created
     */
    public String getAppHome() {
        var AppHome = AppName.toUpperCase() + "_HOME";
        //Default (in case nothing else works) is the current folder
        var HomeFolder = ".";

        //<editor-fold desc="If it exists, return $[APP_NAME_HOME]...">
        if( System.getenv().containsKey(AppHome))
            HomeFolder = System.getenv().get(AppHome);
        //</editor-fold>

        //<editor-fold desc="If not, and its Windows return {%USERPROFILE%\.AppName} ...">
        else if(Platform == OS.Windows) {
            if (System.getenv().containsKey("USERPROFILE")) {
                HomeFolder = System.getenv().get("USERPROFILE") + File.separator +  "." + AppName;
                try {
                    var HomeFolderHandle = new File(HomeFolder);
                    if (!HomeFolderHandle.exists()) {
                        HomeFolderHandle.mkdir();
                    }
                }
                catch(Exception ex) {
                    if(!_recklessMode)
                        throw new Error("Possible IO Error with: " + HomeFolder);
                }
            }
            else {
                if(!_recklessMode)
                    throw new Error("%USERPROFILE% Variable not found");
                else
                    System.err.println("Variable %USERPROFILE% not found.");
            }
        }
        //</editor-fold>

        //<editor-fold desc="Otherwise, {Linux, Mac or Other } return {$HOME/.AppName} ...">
        else {
            if (System.getenv().containsKey("HOME")) {
                HomeFolder = System.getenv().get("HOME") + File.separator +  "." + AppName;
                try {
                    var HomeFolderHandle = new File(HomeFolder);
                    if (!HomeFolderHandle.exists() || !HomeFolderHandle.isDirectory())
                        HomeFolderHandle.mkdir();
                }
                catch(Exception ex) {
                    if(!_recklessMode)
                        throw new Error("Possible IO Error with: " + HomeFolder);
                }
            }
            else {
                if(!_recklessMode)
                    throw new Error("$HOME Variable not found");
                else
                    System.err.println("Variable $HOME not found.");
            }
        }
        //</editor-fold>

        return HomeFolder;
    }
}
