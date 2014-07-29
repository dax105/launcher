package com.jboudny.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Config {

	public static final String DEFAULT_SERVER = "http://www.jboudny.kx.cz/";

	public String server = DEFAULT_SERVER;
	public String username = "";
	public String password = "";

	private File configFile = null;
	private boolean savedCredentials = false;

	public boolean hasSavedCredentials() {
		return this.savedCredentials;
	}

	public Config(File f) {
		this.setConfigFile(f);
	}

	public void setConfigFile(File f) {
		if (f != null) {
			this.configFile = f;

			if (f.exists()) {
				System.out.println("Loading config...");
				load();
				if (!username.equals("") && !password.equals("")) {
					savedCredentials = true;
				}
			} else {
				System.out.println("Config file doesn't exist");
			}
		}
	}

	public void load() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));

			String ln;
			while ((ln = br.readLine()) != null) {
				applySetting(ln);
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(boolean saveCredentials) {
		try {
			PrintWriter pw = new PrintWriter(configFile);

			if (saveCredentials) {
				pw.println("username " + username);
				pw.println("password " + password);
			}

			if (!server.equals(DEFAULT_SERVER)) {
				pw.println("server " + server);
			}

			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException npe) {
			System.out.println("File was not specified");
		}
	}

	public void applySetting(String s) {
		if (s == null || s.isEmpty())
			return;

		String[] words = s.split(" ");

		if (words.length == 2) {
			switch (words[0]) {
			case "server":
				this.server = words[1];
				break;
			case "username":
				this.username = words[1];
				break;
			case "password":
				this.password = words[1];
				break;
			default:
				System.out.println("Invalid setting \"" + s + "\"");
				break;
			}
		}
	}

}
