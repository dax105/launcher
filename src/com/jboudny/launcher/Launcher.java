package com.jboudny.launcher;

import java.awt.Color;
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
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import com.jboudny.launcher.gui.DebugFrame;
import com.jboudny.launcher.gui.MainFrame;

public class Launcher implements Runnable {

	public static Version version = new Version(0, 9, 5);
	public static Version appVersion;
	public static final String DIRECTORY_NAME = "oots";
	public static final String APP_NAME = "Order of the Stone";
	public static final String APP_VERSION_FILE_NAME = "appversion";
	public static final String LAUNCHER_VERSION_FILE_NAME = "lversion";
	public static final String CONFIGURATION_FILE_NAME = "config.cfg";

	private File programFolder = new File(OSUtils.userDataFolder(DIRECTORY_NAME));
	private String appverurl;
	private String launchverurl;
	private Config config;

	private MainFrame mainFrame;
	private DebugFrame debugFrame;

	@Override
	public void run() {
		System.out.println("Current version is: " + version);
		System.out.println("App folder: " + programFolder.getAbsolutePath());

		this.config = new Config(new File(programFolder, Launcher.CONFIGURATION_FILE_NAME));
		appverurl = config.server + "appversion.txt";
		launchverurl = config.server + "launcherversion.txt";
		
		boolean saved = config.hasSavedCredentials();
		
		this.checkDirectory();
		this.checkLauncher();
		this.startGui(saved);
	
		appVersion = getVersion(new File(programFolder, Launcher.APP_VERSION_FILE_NAME));
		
		this.doUpdating();
	}
	
	public void doLoginAndRun(boolean login, String username, String password) {
		if(login) {
			//TODO: Token
			this.runApp(this.mainFrame, username + " " + password);
		} else {
			this.runApp(this.mainFrame, "");
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
			System.out.println("Latest launcher version on server is: "
					+ verlaunch);

			verapp = Version.parseVersion(sa.nextLine());
			System.out.println("Latest app version on server is: " + verapp);

			sa.close();
			sl.close();

			try {
				boolean updated = false;

				if (verlaunch.isNewerThan(version)) {
					updated = true;
					this.mainFrame.setProgressBarText("Downloading new version of launcher (" + verlaunch + ")...");

					update(config.server + "latestlauncher.jar", new File(
							programFolder, "launcher.jar"), verlaunch,
							new File(programFolder, Launcher.LAUNCHER_VERSION_FILE_NAME),
							this.mainFrame.getProgressBar(), true);

					Version li = getVersion(new File(programFolder, Launcher.LAUNCHER_VERSION_FILE_NAME));

					if (li != null && li.isNewerThan(version)) {

						File launcher = new File(programFolder, "launcher" + li
								+ ".jar");

						if (launcher.exists()) {
							System.out
									.println("Switching to the newly downloaded version of launcher...");

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

				}

				if (verapp.isNewerThan(appVersion)) {
					updated = true;
					this.mainFrame.setProgressBarText("Downloading new version of application ("
									+ verapp + ")...");
					update(config.server + "latestapp.jar", new File(programFolder,
							"app.jar"), verapp, new File(programFolder,
							Launcher.APP_VERSION_FILE_NAME), this.mainFrame.getProgressBar(), false);
				}

				this.mainFrame.getProgressBar().setMaximum(100);

				if (!updated) {
					this.mainFrame.setError("No updates found, starting application...");
				} else {
					this.mainFrame.setError("Update done, starting application...");
				}

			} catch (Exception e) {
				this.mainFrame.setError("An error occured while downloading updates, starting application...");
			}

		} catch (Exception ex) {
			this.mainFrame.setError("An error occured while checking for updates, starting application...");
			ex.printStackTrace();
		}
	}

	public void checkDirectory() {
		if (!programFolder.exists()) {
			System.out.println("Directory not found, creating...");
			try {
				programFolder.mkdir();
				System.out.println("Directory created!");
			} catch (SecurityException se) {
				System.out
						.println("Couldn't create app directory, please run this launcher with administrator rights");
				System.exit(0);
			}
		}
	}

	public void checkLauncher() {
		Version li = getVersion(new File(programFolder, Launcher.LAUNCHER_VERSION_FILE_NAME));
		File rf = new File(System.getProperty("user.dir"));
		if (!rf.equals(programFolder)) {
			if (li != null && li.isNewerThan(version)) {
				File launcher = new File(programFolder, "launcher" + li
						+ ".jar");

				if (launcher.exists()) {
					System.out
							.println("Found newer launcher in app folder, swapping...");

					try {
						Runtime.getRuntime().exec(
								"java -jar " + launcher.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.exit(0);

				} else {
					System.out
							.println("Couldn't find newer version of launcher in app folder, continuing...");
				}
			} else {
				System.out
						.println("Couldn't find newer version of launcher in app folder, continuing...");
			}
		} else {
			System.out.println("Already running in program folder!");
		}
	}

	public void startGui(boolean justLogo) {
		Launcher.appVersion = getVersion(new File(programFolder, Launcher.APP_VERSION_FILE_NAME));
		
		this.mainFrame = new MainFrame(this, justLogo);
		this.mainFrame.initControls();
		this.mainFrame.setVisible(true);

		try {
			Thread.sleep(1000000); // Wait for the splash animation to finish :P
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			throw new Exception("Error while downloading update");
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
			runCommand("java -jar " + new File(this.programFolder, "app.jar").getAbsolutePath() + " " + jarParams,
					new IProcessExitCallback() {

						@Override
						public void onExit(int exitCode) {
							if (exitCode == 0) {
								System.out.println("APPLICATION EXITED NORMALLY, EXIT CODE: 0");
							} else {
								System.out.println("APPLICATION HAS CRASHED, EXIT CODE: "
										+ exitCode);
								System.out
										.println("PLEASE SEND THIS LOG TO THE AUTHOR TO FIX THE ISSUE!");
							}
						}
				
			});


		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void runCommand(String cmd, IProcessExitCallback onExit) throws IOException {
		Process proc = Runtime.getRuntime().exec(cmd);			

		StreamGobbler iS = new StreamGobbler(proc.getInputStream(), proc, onExit);
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
    
    public StreamGobbler(InputStream is, Process process, IProcessExitCallback callback) {
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
            
            if(callback != null)
            	callback.onExit(p.exitValue());
            br.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

interface IProcessExitCallback {
	public void onExit(int exitCode);
}
