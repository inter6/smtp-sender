package com.inter6.mail.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ObjectUtil {

	public static String defaultString(Object str, String defaultStr) {
		if (str == null) {
			return defaultStr;
		}
		String s = (String) str;
		return StringUtils.defaultString(s, defaultStr);
	}

	public static boolean defaultBoolean(Object bool, boolean defaultBool) {
		if (bool == null) {
			return defaultBool;
		}
		Boolean b = (Boolean) bool;
		return BooleanUtils.toBooleanDefaultIfNull(b, defaultBool);
	}
}
