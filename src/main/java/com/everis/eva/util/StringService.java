package com.everis.eva.util;

import org.apache.commons.lang3.StringUtils;

public class StringService {

	public static String replaceHTTPRequestValue(String value) {
		final String EMPTY = "";
		String replacedValue = EMPTY;
		
		if (!StringUtils.isBlank(value)) {
			replacedValue = value.replace("\r", EMPTY).replace("%0d", EMPTY).replace("%0D", EMPTY).replace("\n", EMPTY)
					.replace("%0a", EMPTY).replace("%0A", EMPTY);
		}
		return replacedValue;
	}

}
