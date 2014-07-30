package com.jboudny.launcher.localization;

import java.util.Locale;

public class LocalizationHelper {
	public static ILocalization getBestLocalization() {
		Locale l = Locale.getDefault();
		
		System.out.println("Locale " + l.getCountry());
		switch(l.getCountry()) {
		case "CZ":
		case "SK":
			return CsLocalization.getLocalization();
		default:
			return EnLocalization.getLocalization();
		}
	}
}
