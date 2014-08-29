package com.jboudny.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import net.lingala.zip4j.exception.ZipException;

import com.jboudny.launcher.Authentication.AuthStatus;
import com.jboudny.launcher.gui.DebugFrame;
import com.jboudny.launcher.gui.LogoPanelLogin;
import com.jboudny.launcher.gui.MainFrame;
import com.jboudny.launcher.localization.*;

public class Launcher implements Runnable {

	public static Version version = new Version(1, 6, 0);
	public static Version appVersion;
	public static String APP_NAME = "Order of the Stone";

	public static final String DIRECTORY_NAME = "oots";
	public static final String APP_VERSION_FILE_NAME = "appversion";
	public static final String LAUNCHER_VERSION_FILE_NAME = "lversion";
	public static final String CONFIGURATION_FILE_NAME = "config.cfg";

	private File programFolder = new File(
			OSUtils.userDataFolder(DIRECTORY_NAME));
	private File appFile = new File(programFolder, "app.jar");
	private File appVersionFile = new File(programFolder, APP_VERSION_FILE_NAME);
	
	private String appverurl;
	private String launchverurl;
	private Config config;

	private MainFrame mainFrame;
	private DebugFrame debugFrame;

	private boolean saved;

	public boolean isSaved() {
		return saved;
	}

	private ILocalization local;

	public Config getConfig() {
		return config;
	}

	@Override
	public void run() {
		this.local = LocalizationHelper.getBestLocalization();
		Launcher.APP_NAME = this.local.applicationName();

		System.out.println(this.local.launcherVersion(version));
		System.out.println(this.local.launcherFolder(programFolder));

		this.config = new Config(new File(programFolder,
				Launcher.CONFIGURATION_FILE_NAME));
		appverurl = config.server + "appversion.txt";
		launchverurl = config.server + "launcherversion.txt";

		this.saved = config.hasSavedCredentials();
		//this.saved = false;

		this.checkDirectory();
		this.checkLauncher();
		this.startGui(this.saved);

		appVersion = getVersion(this.appVersionFile);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.doUpdating();
		this.checkNatives();
	}

	public boolean doLoginAndRun(String username, String password) {
		String token = this.authenticate(username, password);

		if (token == null)
			return false;

		this.config.username = username;
		this.config.password = password;
		this.runApp(this.mainFrame, username + " " + password + " " + token);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	private String authenticate(String username, String password) {
		Authentication au = new Authentication();
		AuthStatus res = au.authenticate(username, password);

		switch (res) {
		case ERROR_BAD:
			JOptionPane.showMessageDialog(mainFrame, this.local.badLogin());
			return null;
		case ERROR_OTHER:
			JOptionPane.showMessageDialog(mainFrame, this.local.otherError());
			return null;
		case FINE:
			return au.getToken();
		default:
			return null;
		}
	}

	public void checkNatives() {
		File natDir = new File(this.programFolder, "natives");
		if (!(natDir.exists() && natDir.isDirectory())) {
			this.mainFrame.setBarText(this.local.downloadingNatives());

			NativesDownloader down = new NativesDownloader(natDir,
					config.server);
			if (down.downloadNatives(this.mainFrame.getProgressBar())) {
				try {
					down.unpackNatives();
				} catch (ZipException e) {
					e.printStackTrace();

					if (this.mainFrame.getLogoPanel() instanceof LogoPanelLogin) {
						((LogoPanelLogin) this.mainFrame.getLogoPanel())
								.setButtonEnabled(false);
						this.mainFrame.setBarText(this.local
								.cantContinueNatives());
					}
				}
			} else {
				if (this.mainFrame.getLogoPanel() instanceof LogoPanelLogin) {
					((LogoPanelLogin) this.mainFrame.getLogoPanel())
							.setButtonEnabled(false);
					this.mainFrame.setBarText(this.local.cantContinueNatives());
				}
			}
		}
	}

	public void doUpdating() {
		try {
			Version verapp;
			Version verlaunch;

			URL urla = new URL(appverurl);
			URL urll = new URL(launchverurl);

			Scanner sa = new Scanner(urla.openStream());
			Scanner sl = new Scanner(urll.openStream());

			verlaunch = Version.parseVersion(sl.nextLine());
			System.out.println(this.local.latestLauncherVersion(verlaunch));

			verapp = Version.parseVersion(sa.nextLine());
			System.out.println(this.local.latestAppVersion(verapp));

			sa.close();
			sl.close();
			
			this.mainFrame.setControlsEnabled(true);

			try {
				boolean updated = false;

				if (verlaunch.isNewerThan(version)) {
					updated = true;
					this.mainFrame.setControlsEnabled(false);
					this.mainFrame.setProgressBarText(this.local
							.downloadingLatestLauncher(verlaunch));

					update(config.server + "latestlauncher.jar", new File(
							programFolder, "launcher.jar"), verlaunch,
							new File(programFolder,
									Launcher.LAUNCHER_VERSION_FILE_NAME),
							this.mainFrame.getProgressBar(), true);

					Version li = getVersion(new File(programFolder,
							Launcher.LAUNCHER_VERSION_FILE_NAME));

					if (li != null && li.isNewerThan(version)) {

						File launcher = new File(programFolder, "launcher" + li
								+ ".jar");

						if (launcher.exists()) {
							System.out.println(this.local
									.switchingToNewLauncher());

							try {
								Runtime.getRuntime().exec(
										"java -jar "
												+ launcher.getAbsolutePath());
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);

						}
					}
					
					this.mainFrame.setControlsEnabled(true);

				}

				if (verapp.isNewerThan(appVersion) || !this.appFile.exists()) {
					updated = true;
					this.mainFrame.setControlsEnabled(false);
					this.mainFrame.setProgressBarText(this.local
							.downloadingLatestApp(verapp));
					update(config.server + "latestapp.jar", this.appFile, verapp, new File(
							programFolder, Launcher.APP_VERSION_FILE_NAME),
							this.mainFrame.getProgressBar(), false);
					
					this.mainFrame.setControlsEnabled(true);
				}

				this.mainFrame.getProgressBar().setMaximum(100);

				if (!updated) {
					this.mainFrame.setBarText(this.local.noUpdatesStarting());
				} else {
					this.mainFrame.setBarText(this.local.updatesDoneStarting());
				}

			} catch (Exception e) {
				this.mainFrame
						.setBarText(this.local.errorDownloadingStarting());
				e.printStackTrace();
			}

		} catch (Exception ex) {
			this.mainFrame.setBarText(this.local.errorUpdatingStarting());
			ex.printStackTrace();
		}
	}

	public void checkDirectory() {
		if (!programFolder.exists()) {
			System.out.println(this.local.directoryNotFound());
			try {
				programFolder.mkdir();
				System.out.println(this.local.directoryCreated());
			} catch (SecurityException se) {
				System.out.println(this.local.errorCreatingDirectory());
				System.exit(0);
			}
		}
	}

	public void checkLauncher() {
		Version li = getVersion(new File(programFolder,
				Launcher.LAUNCHER_VERSION_FILE_NAME));
		File rf = new File(System.getProperty("user.dir"));
		if (!rf.equals(programFolder)) {
			if (li != null && li.isNewerThan(version)) {
				File launcher = new File(programFolder, "launcher" + li
						+ ".jar");

				if (launcher.exists()) {
					System.out.println(this.local.newerLauncherSwapping());

					try {
						Runtime.getRuntime().exec(
								"java -jar " + launcher.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.exit(0);

				} else {
					System.out.println(this.local
							.newerLauncherDontFoundContinue());
				}
			} else {
				System.out.println(this.local.newerLauncherDontFoundContinue());
			}
		} else {
			System.out.println(this.local.runningInProgramFolder());
		}
	}

	public void startGui(boolean saved) {
		Launcher.appVersion = getVersion(this.appVersionFile);
		
		this.mainFrame = new MainFrame();
		this.mainFrame.useLoginPanel(this, saved ? this.config.username : null, saved ? this.config.password : null);
		this.mainFrame.initControls();
		this.mainFrame.setVisible(true);
	}

	public void update(String url, File f, Version newVersion,
			File versionFile, JProgressBar pb, boolean isLauncher)
			throws Exception {
		try {
			URL website = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) website
					.openConnection();
			connection.connect();

			int contentLength = -1;

			if (connection.getResponseCode() / 100 == 2) {
				contentLength = connection.getContentLength();
			}

			if (contentLength <= 0) {
				pb.setIndeterminate(true);
			} else {
				pb.setIndeterminate(false);
				pb.setMaximum(contentLength);
			}

			FileOutputStream fos = null;

			if (!isLauncher) {
				fos = new FileOutputStream(new File(f.getAbsolutePath() + "z"));
			} else {

				String fname = f.getAbsolutePath();
				int pos = fname.lastIndexOf(".");
				if (pos > 0) {
					fname = fname.substring(0, pos);
				}

				System.out.println(fname);

				fos = new FileOutputStream(
						new File(fname + newVersion + ".jar"));
			}

			int val = 0;

			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(website.openStream());

				final byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1) {
					fos.write(data, 0, count);
					fos.flush();
					val += 1024;
					pb.setValue(val);

				}
				pb.setIndeterminate(true);

				in.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getStackTrace(),
						"Exception during update: " + e,
						JOptionPane.ERROR_MESSAGE);
			}

			if (f.exists() && !isLauncher) {
				f.delete();
			}

			if (!isLauncher) {
				new File(f.getAbsolutePath() + "z").renameTo(f
						.getAbsoluteFile());
			}

			setVersion(versionFile, newVersion);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(this.local.errorDownloading());
		}
	}

	public Version getVersion(File file) {
		try {
			Scanner s = new Scanner(file);

			String ln = s.nextLine();

			s.close();

			try {
				return Version.parseVersion(ln);
			} catch (Exception e) {
				e.printStackTrace();
				return new Version(-1, -1, -1);
			}

		} catch (FileNotFoundException e) {
			return null;
		}

	}

	public void setVersion(File file, Version version) {
		if (file.exists()) {
			file.delete();
		}

		try {
			PrintWriter pw = new PrintWriter(file);
			pw.println(version);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runApp(JFrame frameToDispose, String jarParams) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frameToDispose.dispose();

		this.debugFrame = new DebugFrame();
		this.debugFrame.setVisible(true);

		try {
			runJar(new File(this.programFolder, "app.jar").getAbsolutePath(),
					jarParams, new IProcessExitCallback() {

						@Override
						public void onExit(int exitCode) {
							if (exitCode == 0) {
								System.out.println(local.normalProcessEnd());
							} else {
								System.out.println(local
										.errorProcessEnd(exitCode));
							}
						}

					});

		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void runJar(String jarFile, String jarParams,
			IProcessExitCallback onExit) throws IOException {
		JarLauncher l = new JarLauncher(jarFile);
		l.setMinMemory(OSUtils.getFreeRam() / 4);
		l.setMaxMemory(OSUtils.getFreeRam() / 2);
		l.setNativesDir("natives");
		l.setAppArgs(jarParams);

		Process proc = l.runJar(this.programFolder);

		StreamGobbler iS = new StreamGobbler(proc.getInputStream(), proc,
				onExit);
		StreamGobbler eS = new StreamGobbler(proc.getErrorStream(), proc, null);

		iS.start();
		eS.start();
	}

	public static void main(String[] args) {
		Launcher l = new Launcher();
		Thread t = new Thread(l);
		t.start();
	}

}

class StreamGobbler extends Thread {
	InputStream is;
	IProcessExitCallback callback;
	Process p;

	public StreamGobbler(InputStream is, Process process,
			IProcessExitCallback callback) {
		this.is = is;
		this.callback = callback;
		this.p = process;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				System.out.println(line);

			p.destroy();

			if (callback != null) {
				while (OSUtils.isProcessRunning(p)) {
					;
				}
				callback.onExit(p.exitValue());
			}

			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}

interface IProcessExitCallback {
	public void onExit(int exitCode);
}
