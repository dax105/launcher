package com.jboudny.launcher.localization;

import java.util.Locale;

public class LocalizationHelper {
	private static CsLocalization cz = new CsLocalization();
	private static EnLocalization en = new EnLocalization();
	private static SkLocalization sk = new SkLocalization();
	
	private static String force = null;
	
	public static void setForcelang(String force) {
		LocalizationHelper.force = force;
	}
	
	public static ILocalization getBestLocalization() {
		Locale l = Locale.getDefault();
		String country = !force.equals("") ? force : l.getCountry();
		
		switch(country) {
		case "CZ":
			return LocalizationHelper.cz;
		case "SK":
			return LocalizationHelper.sk;	
		default:
			return LocalizationHelper.en;
		}
	}
}
