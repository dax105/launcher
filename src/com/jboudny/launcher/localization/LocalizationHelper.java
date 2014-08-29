package com.jboudny.launcher.localization;

import java.util.Locale;

public class LocalizationHelper {
	private static CsLocalization cz = new CsLocalization();
	private static EnLocalization en = new EnLocalization();
	private static SkLocalization sk = new SkLocalization();
	
	public static ILocalization getBestLocalization() {
		Locale l = Locale.getDefault();
		switch(l.getCountry()) {
		case "CZ":
			return LocalizationHelper.cz;
		case "SK":
			return LocalizationHelper.sk;
		default:
			return LocalizationHelper.en;
		}
	}
}
