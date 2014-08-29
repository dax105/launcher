package com.jboudny.launcher.localization;

import java.util.Locale;

public class LocalizationHelper {
	public static ILocalization getBestLocalization() {
		Locale l = Locale.getDefault();
		switch(l.getCountry()) {
		case "CZ":
			return CsLocalization.getLocalization();
		case "SK":
			return CsLocalization.getLocalization();
		default:
			return EnLocalization.getLocalization();
		}
	}
}
