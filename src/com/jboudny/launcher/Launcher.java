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

	public static Version version = new Version(0, 9, 5);
	public static final String directoryName = "oots";
	

	private File programFolder = new File(OSUtils.userDataFolder(directoryName));
	private String server = "http://www.jboudny.kx.cz/";
	private String appverurl = server + "appversion.txt";
	private String launchverurl = server + "launcherversion.txt";
	private Version appVersion;
	private JProgressBar progressBar;
	private JFrame frame;

	@Override
	public void run() {
		System.out.println("Current version is: " + version);
		System.out.println("App folder: " + programFolder.getAbsolutePath());

		this.checkDirectory();
		this.checkLauncher();
		this.startGui();
		
		appVersion = getVersion(new File(programFolder, "ppversion"));
		
		this.doWork();
	}
	
	public void doWork() {
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
					this.progressBar
							.setString("Downloading new version of launcher ("
									+ verlaunch + ")...");

					update(server + "latestlauncher.jar", new File(
							programFolder, "launcher.jar"), verlaunch,
							new File(programFolder, "lversion"),
							this.progressBar, true);

					Version li = getVersion(new File(programFolder, "lversion"));

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
					this.progressBar
							.setString("Downloading new version of application ("
									+ verapp + ")...");
					update(server + "latestapp.jar", new File(programFolder,
							"app.jar"), verapp, new File(programFolder,
							"ppversion"), this.progressBar, false);
				}

				this.progressBar.setMaximum(100);

				if (!updated) {
					this.progressBar.setIndeterminate(false);
					this.progressBar
							.setString("No updates found, starting application...");
					this.progressBar.setValue(100);
				} else {
					this.progressBar.setIndeterminate(false);
					this.progressBar
							.setString("Update done, starting application...");
					this.progressBar.setValue(100);
				}

				runApp(frame);

			} catch (Exception e) {
				this.progressBar.setIndeterminate(false);
				this.progressBar
						.setString("An error occured while downloading updates, starting application...");
				this.progressBar.setValue(100);
			}

		} catch (Exception ex) {

			this.progressBar.setIndeterminate(false);
			this.progressBar
					.setString("An error occured while checking for updates, starting application...");
			this.progressBar.setValue(100);

			runApp(frame);

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
		Version li = getVersion(new File(programFolder, "lversion"));
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

	public void startGui() {
		Version appVersion = getVersion(new File(programFolder, "aversion"));

		UIManager.put("ProgressBar.background", Color.GRAY);
		UIManager.put("ProgressBar.foreground", Color.DARK_GRAY);
		UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
		UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
		UIManager.put("ProgressBar.repaintInterval", new Integer(20));

		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setStringPainted(true);
		this.progressBar.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
				Color.BLACK));
		this.progressBar.setUI(new BasicProgressBarUI());
		this.progressBar.setValue(0);

		LogoPanel lp = new LogoPanel();
		if (appVersion.i0 >= 0) {
			lp.appVersion = "" + appVersion;
		}

		lp.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		BorderLayout bl = new BorderLayout();
		this.progressBar.setFont(lp.infoFont);

		this.frame = new JFrame("Order of the Stone Launcher v" + appVersion);

		this.frame.setUndecorated(true);
		this.frame.setSize(600, 320);
		this.frame.setLocationRelativeTo(null);
		this.frame.setBackground(Color.WHITE);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(bl);
		this.frame.add(lp, BorderLayout.CENTER);
		this.frame.add(this.progressBar, BorderLayout.SOUTH);
		this.frame.setVisible(true);

		this.progressBar.setString("Looking for updates...");
		this.progressBar.setIndeterminate(true);

		try {
			Thread.sleep(1000); // Wait for the splash animation to finish :P
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

			int eval = runCommand("java -jar " + new File(this.programFolder, "app.jar").getAbsolutePath());
			if (eval == 0) {
				System.out.println("APPLICATION EXITED NORMALLY, EXIT CODE: 0");
				System.exit(0);
			} else {

				frame.setVisible(true);

				System.out.println("APPLICATION HAS CRASHED, EXIT CODE: "
						+ eval);
				System.out
						.println("PLEASE SEND THIS LOG TO THE AUTHOR TO FIX THE ISSUE!");
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
