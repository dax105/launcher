package com.jboudny.launcher.localization;

import java.io.File;

import com.jboudny.launcher.Version;

public class CsLocalization implements ILocalization {

	private static CsLocalization instance = null;
	public static CsLocalization getLocalization() {
		if(instance == null)
			instance = new CsLocalization();
		
		return instance;
	}
	
	private CsLocalization() {
		
	}
	
	@Override
	public String launcherVersion(Version version) {
		return "Verze launcheru: " + version;
	}
	
	@Override
	public String applicationVersion(Version version) {
		return "Verze hry: " + version;
	}

	@Override
	public String launcherFolder(File folder) {
		return "Datový adresář: " + folder.getAbsolutePath();
	}

	@Override
	public String applicationName() {
		return "Objednávka kamene";
	}

	@Override
	public String badLogin() {
		return "Špatné uživatelské jméno nebo heslo";
	}

	@Override
	public String otherError() {
		return "Chyba!";
	}

	@Override
	public String latestLauncherVersion(Version version) {
		return "Nejnovější verze launcheru: " + version;
	}

	@Override
	public String latestAppVersion(Version version) {
		return "Nejnovější verze hry: " + version;
	}

	@Override
	public String downloadingLatestLauncher(Version version) {
		return "Stahování nejnovějšího launcheru verze " + version;
	}

	@Override
	public String downloadingLatestApp(Version version) {
		return "Stahování nejnovější hry verze " + version;
	}

	@Override
	public String switchingToNewLauncher() {
		return "Spouštím nejnovější verzi launcheru";
	}

	@Override
	public String noUpdatesStarting() {
		return "Aktualizace nenalezeny";
	}

	@Override
	public String updatesDoneStarting() {
		return "Aktualizováno";
	}

	@Override
	public String errorDownloadingStarting() {
		return "Chyba při stahování aktualizací";
	}

	@Override
	public String errorUpdatingStarting() {
		return "Chyba při zjišťování aktualizací";
	}

	@Override
	public String directoryNotFound() {
		return "Adresář nenalezen";
	}

	@Override
	public String directoryCreated() {
		return "Adresář vytvořen";
	}

	@Override
	public String errorCreatingDirectory() {
		return "Chyba při vytváření adresáře, zkuste spustit aplikaci s administrátorskými právy.";
	}

	@Override
	public String newerLauncherSwapping() {
		return "V adresáři hry nalezen novější launcher, spouštím";
	}

	@Override
	public String newerLauncherDontFoundContinue() {
		return "Novější launcher nenalezen";
	}

	@Override
	public String runningInProgramFolder() {
		return "Launcher běží v adresáři hry";
	}

	@Override
	public String errorDownloading() {
		return "Chyba při stahování aktualizace";
	}
	
	@Override
	public String errorDownloadingNatives() {
		return "Chyba při stahování knihoven";
	}

	@Override
	public String normalProcessEnd() {
		return "APLIKACE SKONCILA BEZ CHYB";
	}

	@Override
	public String errorProcessEnd(int number) {
		return "APLIKACE SKONCILA S CHYBOVYM KODEM " + number + "!";
	}

	@Override
	public String userName() {
		return "Uživatelské jméno";
	}

	@Override
	public String password() {
		return "Heslo";
	}

	@Override
	public String rememberCredentials() {
		return "Zapamatovat si mě";
	}

	@Override
	public String loggingIn() {
		return "Přihlašování";
	}
	
	@Override
	public String loginButton() {
		return "Přihlásit";
	}

	@Override
	public String lookingForUpdates() {
		return "Zjišťuji aktualizace";
	}

	@Override
	public String cantContinueNatives() {
		return "Nelze pokračovat - chybí knihovny";
	}

	@Override
	public String downloadingNatives() {
		return "Stahuji knihovny";
	}

}
