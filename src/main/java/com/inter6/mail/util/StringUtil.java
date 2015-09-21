package com.inter6.mail.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class StringUtil {

	public static Set<String> splitToSet(String str, String separator) {
		Set<String> set = new HashSet<String>();
		if (StringUtils.isBlank(str)) {
			return set;
		}
		String[] tokens = str.split(separator);
		if (ArrayUtils.isNotEmpty(tokens)) {
			for (String token : tokens) {
				set.add(token);
			}
		}
		return set;
	}
}
