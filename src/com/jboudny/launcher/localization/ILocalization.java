package com.jboudny.launcher.localization;

import java.io.File;

import com.jboudny.launcher.Version;

public interface ILocalization {
	public String launcherVersion(Version version);
	public String applicationVersion(Version version);
	public String latestLauncherVersion(Version version);
	public String latestAppVersion(Version version);
	
	public String downloadingLatestLauncher(Version version);
	public String downloadingLatestApp(Version version);
	public String downloadingNatives();
	
	public String normalProcessEnd();
	public String errorProcessEnd(int number);
	public String errorDownloading();
	public String errorDownloadingNatives();
	public String errorDownloadingStarting();
	public String errorUpdatingStarting();
	public String errorCreatingDirectory();
	public String cantContinueNatives();
	public String badLogin();
	public String otherError();
	
	public String directoryNotFound();
	public String directoryCreated();
	public String launcherFolder(File folder);
	
	public String applicationName();
	public String switchingToNewLauncher();
	public String runningInProgramFolder();
	
	public String noUpdatesStarting();
	public String updatesDoneStarting();
	public String newerLauncherSwapping();
	public String newerLauncherNotFoundContinue();
	
	public String userName();
	public String password();
	public String rememberCredentials();
	public String loggingIn();
	public String loginButton();
	public String lookingForUpdates();
}
