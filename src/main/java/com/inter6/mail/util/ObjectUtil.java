package com.inter6.mail.util;

public class ObjectUtil {

    public static String defaultString(Object str, String defaultStr) {
        if (str == null) {
            return defaultStr;
        }
        return (String) str;
    }

    public static boolean defaultBoolean(Object bool, boolean defaultBool) {
        if (bool == null) {
            return defaultBool;
        }
        return (Boolean) bool;
    }
}
