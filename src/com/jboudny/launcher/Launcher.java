package com.jboudny.launcher;

import java.awt.BorderLayout;
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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class Launcher implements Runnable {

	public static Version version = new Version(0, 9, 6);

	@Override
	public void run() {

		System.out.println("Current version is: " + version);

		File programFolder = new File(System.getenv("APPDATA") + "\\oots");

		System.out.println("App folder: " + programFolder.getAbsolutePath());

		if (!programFolder.exists()) {
			System.out.println("Directory not found, creating...");
			boolean result = false;

			try {
				programFolder.mkdir();
				result = true;
			} catch (SecurityException se) {
				System.out.println("Couldn't create app directory, please run this launcher with administrator rights");
				System.exit(0);
			}
			if (result) {
				System.out.println("Directory created!");
			}
		} else {

			// File launcher = new File(programFolder.getAbsolutePath() +
			// "\\launcher.jar");
			Version li = getVersion(new File(programFolder.getAbsolutePath() + "\\launcherversion.txt"));

			File rf = new File(System.getProperty("user.dir"));

			if (!rf.equals(programFolder)) {

				if (li != null && li.isNewerThan(version)) {

					File launcher = new File(programFolder.getAbsolutePath() + "\\launcher" + li + ".jar");

					if (launcher.exists()) {
						System.out.println("Found newer launcher in app folder, swapping...");

						try {
							Runtime.getRuntime().exec("java -jar " + "\"" + launcher.getAbsolutePath() + "\"");
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);

					} else {
						System.out.println("Couldn't find newer version of launcher in app folder, continuing...");
					}
				} else {
					System.out.println("Couldn't find newer version of launcher in app folder, continuing...");
				}

			} else {
				System.out.println("Already running in program folder!");
			}

		}

		Version appVersion = getVersion(new File(programFolder.getAbsolutePath() + "\\appversion.txt"));

		UIManager.put("ProgressBar.background", Color.GRAY);
		UIManager.put("ProgressBar.foreground", Color.DARK_GRAY);
		UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
		UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
		UIManager.put("ProgressBar.repaintInterval", new Integer(20));
		// UIManager.put("ProgressBar.cycleTime", new Integer(5000));

		JProgressBar jpb = new JProgressBar(0, 100);
		jpb.setStringPainted(true);

		jpb.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));

		jpb.setUI(new BasicProgressBarUI());

		// jpb.setIndeterminate(true);

		jpb.setValue(0);

		LogoPanel lp = new LogoPanel();

		if (appVersion.i0 >= 0) {
			lp.appVersion = "" + appVersion;
		}

		lp.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		BorderLayout bl = new BorderLayout();

		jpb.setFont(lp.infoFont);

		JFrame frame = new JFrame("Order of the stone launcher");

		frame.setUndecorated(true);
		frame.setSize(600, 320);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(bl);
		frame.add(lp, BorderLayout.CENTER);
		frame.add(jpb, BorderLayout.SOUTH);
		frame.setVisible(true);

		jpb.setString("Looking for updates...");

		jpb.setIndeterminate(true);

		try {
			Thread.sleep(1000); // Wait for the splash animation to finish :P
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String server = "http://www.jboudny.kx.cz/";
		String appverurl = server + "appversion.txt";
		String launchverurl = server + "launcherversion.txt";

		try {
			Version verapp;
			Version verlaunch;

			URL urla = new URL(appverurl);
			URL urll = new URL(launchverurl);

			Scanner sa = new Scanner(urla.openStream());
			Scanner sl = new Scanner(urll.openStream());

			verlaunch = Version.parseVersion(sl.nextLine());
			System.out.println("Latest launcher version on server is: " + verlaunch);

			verapp = Version.parseVersion(sa.nextLine());
			System.out.println("Latest app version on server is: " + verapp);

			sa.close();
			sl.close();

			try {
				boolean updated = false;

				if (verlaunch.isNewerThan(version)) {
					updated = true;
					jpb.setString("Downloading new version of launcher (" + verlaunch + ")...");
					update(server + "latestlauncher.jar", new File(programFolder.getAbsolutePath() + "\\launcher.jar"), verlaunch, new File(programFolder.getAbsolutePath() + "\\launcherversion.txt"), jpb, true);

					Version li = getVersion(new File(programFolder.getAbsolutePath() + "\\launcherversion.txt"));

					if (li != null && li.isNewerThan(version)) {

						File launcher = new File(programFolder.getAbsolutePath() + "\\launcher" + li + ".jar");

						if (launcher.exists()) {
							System.out.println("Switching to the newly downloaded version of launcher...");

							try {
								Runtime.getRuntime().exec("java -jar " + "\"" + launcher.getAbsolutePath() + "\"");
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);

						}
					}

				}

				if (verapp.isNewerThan(appVersion)) {
					updated = true;
					jpb.setString("Downloading new version of application (" + verapp + ")...");
					update(server + "latestapp.jar", new File(programFolder.getAbsolutePath() + "\\app.jar"), verapp, new File(programFolder.getAbsolutePath() + "\\appversion.txt"), jpb, false);
				}

				jpb.setMaximum(100);

				if (!updated) {
					jpb.setIndeterminate(false);
					jpb.setString("No updates found, starting application...");
					jpb.setValue(100);
				} else {
					jpb.setIndeterminate(false);
					jpb.setString("Update done, starting application...");
					jpb.setValue(100);
				}

				runApp(frame);

			} catch (Exception e) {
				jpb.setIndeterminate(false);
				jpb.setString("An error occured while downloading updates, starting application...");
				jpb.setValue(100);
			}

		} catch (Exception ex) {

			jpb.setIndeterminate(false);
			jpb.setString("An error occured while checking for updates, starting application...");
			jpb.setValue(100);

			runApp(frame);

			ex.printStackTrace();
		}

	}

	public void update(String url, File f, Version newVersion, File versionFile, JProgressBar pb, boolean isLauncher) throws Exception {
		try {
			URL website = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) website.openConnection();
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

				fos = new FileOutputStream(new File(fname + newVersion + ".jar"));
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
				JOptionPane.showMessageDialog(null, e.getStackTrace(), "Exception during update: " + e, JOptionPane.ERROR_MESSAGE);
			}

			if (f.exists() && !isLauncher) {
				f.delete();
			}

			if (!isLauncher) {
				new File(f.getAbsolutePath() + "z").renameTo(f.getAbsoluteFile());
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
			return new Version(-1, -1, -1);
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

	public void runApp(JFrame frameToDispose) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		frameToDispose.dispose();

		OutputPanel op = new OutputPanel();

		op.setBorder(BorderFactory.createEmptyBorder());

		JFrame frame = new JFrame("Debug window");
		frame.add(op);
		frame.setSize(600, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		try {

			int eval = runCommand("java -jar " + "\"" + new File(System.getenv("APPDATA") + "\\KarelGL\\app.jar").getAbsolutePath() + "\"");
			if (eval == 0) {
				System.out.println("APPLICATION EXITED NORMALLY, EXIT CODE: 0");
				System.exit(0);
			} else {

				frame.setVisible(true);

				System.out.println("APPLICATION HAS CRASHED, EXIT CODE: " + eval);
				System.out.println("PLEASE SEND THIS LOG TO THE AUTHOR TO FIX THE ISSUE!");
			}

		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static int runCommand(String cmd) throws IOException {
		Process proc = Runtime.getRuntime().exec(cmd);

		InputStream istr = proc.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(istr));
		String str;

		while ((str = br.readLine()) != null) {
			System.out.println(str);
		}

		br.close();

		return proc.exitValue();
	}

	public static void main(String[] args) {
		Launcher l = new Launcher();
		Thread t = new Thread(l);
		t.start();
	}

}
