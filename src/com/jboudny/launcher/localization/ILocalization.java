package com.jboudny.launcher.localization;

import java.io.File;

import com.jboudny.launcher.Version;

public interface ILocalization {
	public String launcherVersion(Version version);
	public String applicationVersion(Version version);
	public String launcherFolder(File folder);
	public String applicationName();
	public String badLogin();
	public String otherError();
	public String latestLauncherVersion(Version version);
	public String latestAppVersion(Version version);
	public String downloadingLatestLauncher(Version version);
	public String downloadingLatestApp(Version version);
	public String switchingToNewLauncher();
	public String noUpdatesStarting();
	public String updatesDoneStarting();
	public String errorDownloadingStarting();
	public String errorUpdatingStarting();
	public String directoryNotFound();
	public String directoryCreated();
	public String errorCreatingDirectory();
	public String newerLauncherSwapping();
	public String newerLauncherDontFoundContinue();
	public String runningInProgramFolder();
	public String errorDownloading();
	public String normalProcessEnd();
	public String errorProcessEnd(int number);
	public String userName();
	public String password();
	public String rememberCredentials();
	public String loggingIn();
	public String loginButton();
	
}
