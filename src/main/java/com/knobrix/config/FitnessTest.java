package com.knobrix.config;

import package1.Class1;
import package2.Class2;
import package3.Class3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

class FitnessTest {

    public static boolean DEBUG = false;

    private static final String TmpFile = "." +
                                          File.separator +
                                          "extra_config.ini";

    private static final String TmpFile2 = "." + File.separator + "extra_config2.ini";

    private static void configFileSetup() {

        var AppFolder = AppInfo.One.getAppHome();
        try {
            var configDir = new File(AppFolder + File.separator + "config");
            if( !configDir.exists() )
                configDir.mkdir();

              var ConfigFile = AppFolder + File.separator +
                                "config" + File.separator + "props.ini";
              var FileContent =  "package1.Class1:\n" +
                                "  var1=123\n" +
                                "  var2=0.001\n" +
                                "  var4=Windows\n" +
                                "  var3=value3\n\n" +
                                "package2.Class2:\n" +
                                "  param1=--arg1\n" +
                                "  var4=value2\n" +
                                "  java_home=$JAVA_HOME\n\n" +
                                "!" + TmpFile + "\n" +
                                "!--extra_config";

              var writer = new BufferedWriter(new FileWriter(ConfigFile));
              writer.write(FileContent);
              writer.close();

              FileContent = "package2.Class2:\n" +
                            "  var4=NewValue2\n" +
                            "  var5=value5\n" +
                            "package3.Class3:\n" +
                            "  var7=--arg2\n" +
                            "  var8=$JAVA_HOME" ;

             writer = new BufferedWriter(new FileWriter(TmpFile));
             writer.write(FileContent);
             writer.close();

            FileContent = "package2.Class2:\n" +
                          "  var5=value7";
            writer = new BufferedWriter(new FileWriter(TmpFile2));
            writer.write(FileContent);
            writer.close();


        }
        catch(Exception ex) {
             throw new Error(ex.getMessage());
        }
    }

    public static void testAppFolder() {
        //Test Default AppName
        if( !AppInfo.One.getAppName().equals("com_knobrix_config"))
            throw new Error("Expected AppName COM_KNOBRIX_CONFIG. Found: [" + AppInfo.One.getAppName() + "]");
        AppInfo.One.setAppName("config_test");
        var AppFolder = AppInfo.One.getAppHome();
    }

    public static void dumpEnv() {
        System.out.print("Dump Env Vars...  ");
        var Env = System.getenv();
        for(var key : Env.keySet()) {
            if( Env.get(key).length() < 1)
                throw new Error("Env Variable {" + key + "} not found");
           }
        System.out.println("\tPASSED");
    }

    public static void testSimpleLoad() {
        System.out.print("Simple Config...  ");
        String[] test = {
                "package1.Class1:", "var1=value1", "var2=value2", "var3=value3",
                "package2.Class2:", "  param1=--arg1", "  var4=value2", "  java_home=$JAVA_HOME"
        };
        var ret = ConfigLoader.One.load(test);
        for(var curClass : ret.keySet()) {
            for(var curKey : ret.get(curClass).keySet()) {
                if( ret.get(curClass).get(curKey).length() < 1)
                    throw new Error("Env Variable {" + curClass + "."+  curKey + "} not found");
            }
        }
        System.out.println("\tPASSED");
    }

    public static void configFileLoad() {
        System.out.print("File Config...   ");
        configFileSetup();
        var varMap = ConfigLoader.One.load();

        if(DEBUG)
         for(var key1 : varMap.keySet())
            for(var key2 : varMap.get(key1).keySet())
                System.out.println(key1 + "." + key2 + "=" + varMap.get(key1).get(key2));

        var expected = System.getenv().get("JAVA_HOME");
        if(!varMap.containsKey("package2.Class2") || !varMap.get("package2.Class2").containsKey("java_home"))
            throw new Error("package2.Class2.java_home not Found in Config");
        var found = varMap.get("package2.Class2").get("java_home");
        if(!found.equals(expected) )
            throw new Error("Expected: " + expected + ". Found: " + found);

        expected = "value1";
        if(!varMap.containsKey("package2.Class2") || !varMap.get("package2.Class2").containsKey("param1"))
            throw new Error("package2.Class2.param1 not Found in Config");
        found = varMap.get("package2.Class2").get("param1");
        if(!found.equals(expected) )
           throw new Error("Expected: " + expected + ". Found: " + found);

        expected = "value2";
        if(!varMap.containsKey("package3.Class3") || !varMap.get("package3.Class3").containsKey("var7"))
            throw new Error("package3.Class3.var7 not Found in Config");
        found = varMap.get("package3.Class3").get("var7");
        if(!found.equals(expected) )
            throw new Error("Expected: " + expected + ". Found: " + found);

        expected = "value7";
        if(!varMap.containsKey("package2.Class2") || !varMap.get("package2.Class2").containsKey("var5"))
            throw new Error("package2.Class2.var2 not Found in Config");
        found = varMap.get("package2.Class2").get("var5");
        if(!found.equals(expected) )
            throw new Error("Expected: " + expected + ". Found: " + found);
        System.out.println("\tPASSED");
    }

    public static void fullConfigTest(String args[]) {
        System.out.print("Full Config...   ");
        Settings.load(args);
        if( Class1.var1 != 123 ||
                Class1.var2  != 0.001 ||
                !Class1.var3.equals("value3") ||
                Class1.var4 != AppInfo.OS.Windows )
            throw new Error("Class1 values not set up as expected");

        if(!Class2.var4.equals("NewValue2") )
            throw new Error("Class2 values not set up as expected: " + Class2.var4);

        if(!Class3.var7.equals("value2") )
            throw new Error("Class3 values not set up as expected: " + Class3.var7);

        System.out.println("\tPASSED");
    }

    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("REQUIRED:  com.knobrix.config.FitnessTest --arg1:value1 " +
                               "--arg2:value2 --extra_config:.\\extra_config2.ini");
            System.exit(-1);
        }

        if(args.length >= 4) {
            if(args[3].equals("--debug:true"))
                DEBUG = true;
        }

        System.out.println("\n------Config Integration Test------");

        try {
            System.out.print("1. ");
            dumpEnv();
        }
        catch(Error ex) { System.out.println("\tFAILED with: " + ex.getMessage());  }

        //Requires a command line param --arg1:value1 and for $JAVA_HOME to be set
        try {
            ConfigLoader.One.setArgs(args);
            System.out.print("2. ");
            testSimpleLoad();
        }
        catch(Error ex) { System.out.println("\tFAILED with: " + ex.getMessage());  }

        try {
            System.out.print("3. ");
            configFileLoad();
        }
        catch(Error ex) {
            System.out.println("\tFAILED with: " + ex.getMessage());
        }
        try {
            System.out.print("4. ");
            fullConfigTest(args);
        }
        catch(Error ex) {
            System.out.println("\tFAILED with: " + ex.getMessage());
        }
        System.out.println("---------Config Test Done---------\n");

        System.out.println("--App name: " + AppInfo.One.getAppName());
        System.out.println("--Home folder: " + AppInfo.One.getAppHome());
    }
}
