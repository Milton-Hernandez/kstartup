package com.knobrix.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static boolean firstUse = true; 
    
    public static boolean isFirstUse() {
        return firstUse;
    }

    private static String getEnumDef(Class<Enum> enumClass) {
        try {
            Field f = enumClass.getDeclaredField("$VALUES");
            f.setAccessible(true);
            var str = "[";
            var ret = (Object[]) f.get(null);
            for(var o : ret)
                str += "," + o.toString();
            str += "]";
            str = str.replace("[,","[");
            return str;
        }
        catch(Exception ex) {
            return "[]";
        }
    }

    public static void init() {
        var varMap = ConfigLoader.One.load();
        for(var className : varMap.keySet()) {
            var subMap = varMap.get(className);
            setConfig(className, subMap);
        }      
        firstUse = false;
    }
    
    public static void load(String[] args) {
        ConfigLoader.One.setArgs(args);
        init();
    }

    public static void setConfig(String configClassName, Map<String,String> settings) {
        String CurFieldName =  "";
        String CurFieldValue = "";

        try {
            var CurClass = Class.forName(configClassName);

            var fieldMap = new HashMap<String,Field>();
            for(var f : CurClass.getDeclaredFields())
                fieldMap.put(f.getName(), f);


            for(var key : settings.keySet()) {
                CurFieldName = key;
                CurFieldValue = settings.get(CurFieldName);
                if(!fieldMap.containsKey(CurFieldName))
                    throw new Error("CONFIG_ERROR. Field Not Found: " + configClassName + "." + CurFieldName);
                Field CurField = CurClass.getDeclaredField(CurFieldName);

                if(CurField.getType().equals(String.class))
                    CurField.set(CurClass, CurFieldValue);
                else if (CurField.getType().equals(Integer.class))
                    CurField.set(CurClass, Integer.parseInt(CurFieldValue));
                else if (CurField.getType().equals(Double.class))
                    CurField.set(CurClass, Double.parseDouble(CurFieldValue));
                else if (CurField.getType().equals(Boolean.class))
                    CurField.set(CurClass, Boolean.parseBoolean(CurFieldValue));
                else if(CurField.getType().isEnum())
                    try {
                        CurField.set(CurClass, Enum.valueOf((Class<Enum>) CurField.getType(), CurFieldValue));
                    }
                    catch(IllegalArgumentException ex) {
                        var ret = "CONFIG_ERROR: Illegal Option for: " + configClassName + "." + CurFieldName + ". Valid Options are: ";
                        ret += getEnumDef((Class<Enum>) CurField.getType());
                        throw new Error(ret);
                    }
                else
                    throw new Error("CONFIG_ERROR: Illegal Field Type: " + CurField.getType() + " " + CurField.getName());
            }
            
           if(fieldMap.containsKey("dirty")) {
               Field CurField = CurClass.getDeclaredField("dirty");
               CurField.set(CurClass, (Boolean) true);
           }
        }
        catch(IllegalAccessException ex) {
            throw new Error("CONFIG_ERROR: Access Violation in " + configClassName + "." + CurFieldName );
        }
        catch(IllegalArgumentException ex) {
            throw new Error("CONFIG_ERROR: Unsupported Field Type in " + configClassName + "." + CurFieldName );
        }
        catch(NoSuchFieldException ex) {
            
        }
        catch(ClassNotFoundException ex) {
            throw new Error("CONFIG_ERROR: Cannot find class: " + configClassName );
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
