package com.jboudny.launcher.localization;

public class SkLocalization extends CsLocalization {

	private static SkLocalization instance = null;
	public static SkLocalization getLocalization() {
		if(instance == null)
			instance = new SkLocalization();
		
		return instance;
	}
	
	@Override
	public String applicationName() {
		return "Objednávka kameňa";
	}
	
}
