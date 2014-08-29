package com.jboudny.launcher.localization;

import java.io.File;

import com.jboudny.launcher.Version;

public class EnLocalization implements ILocalization {
	
	@Override
	public String launcherVersion(Version version) {
		return "Launcher version is " + version;
	}

	@Override
	public String launcherFolder(File folder) {
		return "App folder: " + folder.getAbsolutePath();
	}

	@Override
	public String applicationName() {
		return "Order of the Stone";
	}

	@Override
	public String badLogin() {
		return "Wrong username or password";
	}

	@Override
	public String otherError() {
		return "Error!";
	}

	@Override
	public String latestLauncherVersion(Version version) {
		return "Latest launcher version on server is: " + version;
	}

	@Override
	public String latestAppVersion(Version version) {
		return "Latest app version on server is: " + version;
	}

	@Override
	public String downloadingLatestLauncher(Version version) {
		return "Downloading new version of launcher (" + version + ")...";
	}

	@Override
	public String downloadingLatestApp(Version version) {
		return "Downloading new version of application (" + version + ")...";
	}

	@Override
	public String switchingToNewLauncher() {
		return "Switching to the newly downloaded version of launcher...";
	}

	@Override
	public String noUpdatesStarting() {
		return "No updates found, waiting for login...";
	}

	@Override
	public String updatesDoneStarting() {
		return "Update done, waiting for login...";
	}

	@Override
	public String errorDownloadingStarting() {
		return "An error occured while downloading updates, waiting for login...";
	}

	@Override
	public String errorUpdatingStarting() {
		return "An error occured while checking for updates, waiting for login...";
	}

	@Override
	public String directoryNotFound() {
		return "Directory not found, creating...";
	}

	@Override
	public String directoryCreated() {
		return "Directory created!";
	}

	@Override
	public String errorCreatingDirectory() {
		return "Couldn't create app directory, please run this launcher with administrator rights";
	}

	@Override
	public String newerLauncherSwapping() {
		return "Found newer launcher in app folder, swapping...";
	}

	@Override
	public String newerLauncherDontFoundContinue() {
		return "Couldn't find newer version of launcher in app folder, continuing...";
	}

	@Override
	public String runningInProgramFolder() {
		return "Already running in program folder!";
	}

	@Override
	public String errorDownloading() {
		return "Error while downloading update";
	}

	@Override
	public String normalProcessEnd() {
		return "APPLICATION EXITED NORMALLY, EXIT CODE: 0";
	}

	@Override
	public String errorProcessEnd(int number) {
		return "APPLICATION HAS CRASHED, EXIT CODE: "
				+ number + "\nPLEASE CONTACT AUTHORS.";
	}

	@Override
	public String userName() {
		return "Username";
	}

	@Override
	public String password() {
		return "Password";
	}

	@Override
	public String rememberCredentials() {
		return "Remember me";
	}

	@Override
	public String loggingIn() {
		return "Logging in...";
	}

	@Override
	public String loginButton() {
		return "Login";
	}
	
	@Override
	public String errorDownloadingNatives() {
		return "Error while downloading natives";
	}

	@Override
	public String applicationVersion(Version version) {
		return "Game version is " + version;
	}

	@Override
	public String lookingForUpdates() {
		return "Looking for updates...";
	}

	@Override
	public String cantContinueNatives() {
		return "Can't continue - natives can't be downloaded";
	}

	@Override
	public String downloadingNatives() {
		return "Downloading natives";
	}
}
