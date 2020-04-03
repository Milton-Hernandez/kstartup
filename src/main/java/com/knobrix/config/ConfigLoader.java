package com.knobrix.config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Utility class that allows you to parse all of the configuration strings
 * and pack them into a nice Map.  Basically,  you'll get a configuration Map<String,Map<String,String>>
 * where the main Keys  contains the Names of the different static configuration classes, and the values
 * are HashMaps for the KeyValuepairs of Field-->Value in  the Config class.  It can also substitute
 * command line values and/or Environment variables.
 *
 * @author Milton Hernandez
 * @copyright Epheriant, LLC, 2020.
 *
 */
public class ConfigLoader {
    /**Safe Singleton*/
    public final static ConfigLoader One = new ConfigLoader();

    //<editor-fold desc="Nutty bits...">
    private final HashMap<String,String> Args = new HashMap<String,String>();
    private final HashSet<String> LoadedFiles = new HashSet<>();
    private ConfigLoader() { }
    private String getConfigFileName() {
        return AppInfo.One.getAppHome() + File.separator +
                "config" + File.separator +
                "props.ini";
    }
    private ArrayList<String> loadLines(String fileName)  {
        var retList = new ArrayList<String>();
        var ConfigFileName = fileName.trim();
        if(LoadedFiles.contains(ConfigFileName))
            throw new Error("CONFIG_ERROR Duplicate or Recursive config: " + ConfigFileName);
        LoadedFiles.add(ConfigFileName);
        try {
            BufferedReader InFile = new BufferedReader(new FileReader(ConfigFileName));
            for (String line = InFile.readLine(); line != null; line = InFile.readLine()) {
                var thisLine = line.trim();
                if (thisLine.length() > 0) {
                    if(thisLine.charAt(0) != '!')
                        retList.add(thisLine);
                    else {
                        var param = thisLine.substring(1);
                        if(param.startsWith("--")) {
                            if (Args.containsKey(param))
                                retList.addAll(loadLines(Args.get(param)));
                            else
                                throw new Error("Runtime Parameter for included config: " + param + " not provided");
                        }
                        else
                            retList.addAll(loadLines(param));
                    }
                }
            }
            return retList;
        }
        catch(FileNotFoundException f) {
            return retList;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            throw new Error("CONFIG_ERROR loading " + ConfigFileName + " : " + ex.getMessage());
        }
    }
    //</editor-fold>

    /**
     * Sets the values of the Command line arguments.  Any value in the configuration file that starts with
     * '--' will be replaced with the corresponding Command Line Argument.  The Key/Value pair in the command
     * line arguments need to be in the format "--arg:value"
     * @param  args of Strings containing command line arguments in the format {"--arg1:value1", "--arg2:value2", ...}
     */
    public void setArgs(String[] args) {
        Args.clear();
        for(var arg : args) {
            var st = new StringTokenizer(arg,":");
            var FieldName =  st.nextToken().trim();
            var FieldValue = st.nextToken().trim();
            Args.put(FieldName,FieldValue);
        }
    }

    /**
     * Loads the configuration string lines from a configuration file.
     * The library will look for a file named './config/properties.ini' in the 'HomeFolder' variable of the
     * AppInfo class.
     * @return An Array of Strings with the contents of the config file
     * @see AppInfo
     * @throws Error, when file is not found
     */
    public String[] loadLines()  {
        LoadedFiles.clear();
        return loadLines(getConfigFileName()).toArray(new String[0]);
    }

    /**
     * Processes an Array of configuration lines from the default config file and returns a Map of Maps
     * where then main Keys are the classes to be set statically, and the values are the KeyValue Pairs of
     * Field, Value for the Map.
     * @return Configuration Map of Maps described above.
     * @throws Error, for duplicate Config Class Names, if runtime parameters are missing or if a required
     * env variable is not found.
     */
    public HashMap<String, HashMap<String,String>> load() {
        return load(loadLines());
    }

    /**
     * Processes an Array of configuration lines (typically from a config file) and returns a Map of Maps
     * where then main Keys are the classes to be set statically, and the values are the KeyValue Pairs of
     * Field, Value for the Map.  It also replaces values starting with '--' by Command line arguments et in
     * Args an it replaces values starting with '$' by the equivalent Case-Matching environment variable. The
     * Configuration format of the line is as follows:
     *         String[] args = {
     *                           "package1.class1:",
     *                             "var1=value1",
     *                             "var2=value2",
     *                             "var3=value3",
     *                           "package2.class2:",
     *                             "runtime1=--arg1",
     *                             "var4=value2",
     *                             "env1=$VAR1",
     *                           "..."
     *                         };
     * @return Configuration Map of Maps described above.
     * @throws Error, for duplicate Config Class Names, if runtime parameters are missing or if a required
     * env variable is not found.
     */
    public HashMap<String, HashMap<String,String>> load(String[] allLines) {
            var RetMap =  new HashMap<String, HashMap<String,String>>();
            var className = "-";

            for(var line : allLines) {
                if(line.endsWith(":")) {
                    className = line.substring(0,line.length()-1);
                    if(!RetMap.containsKey(className))
                        RetMap.put(className,new HashMap<>());
                }
                else if(line.contains("=")) {
                    var st = new StringTokenizer(line,"=");
                    var FieldName =  st.nextToken().trim();
                    var FieldValue = st.nextToken().trim();
                    if(RetMap.containsKey(className)) {
                        if(FieldValue.startsWith("--")) {
                            if(!Args.containsKey(FieldValue))
                                throw new Error("Runtime argument missing: [" + FieldValue  + "]");
                            FieldValue = Args.get(FieldValue);
                        }
                        else if (FieldValue.startsWith("$")) {
                            FieldValue = FieldValue.substring(1);
                            if(!System.getenv().containsKey(FieldValue))
                                throw new Error("Missing required environment variable: [" + FieldValue + "]");
                            FieldValue = System.getenv().get(FieldValue);
                        }
                        RetMap.get(className).put(FieldName, FieldValue);
                    }
                    else
                        throw new Error("Class Name: [" + className + "] not found");
                }
            }
            return RetMap;
    }
}
