package com.jboudny.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

public class Config {

	public static final String DEFAULT_SERVER = "http://ondryasondra.aspone.cz/";

	public String server = DEFAULT_SERVER;
	public String username = "";
	public String password = "";
	public String forcelang = "";
	public int startMemory = 0;
	public int maxMemory = 0;

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
				pw.println("password " + this.encodePassword(password));
			}

			if (!this.server.equals(DEFAULT_SERVER)) {
				pw.println("server " + server);
			}
			
			if(!this.forcelang.equals("")) {
				pw.println("forcelang " + forcelang);
			}
			
			if(this.maxMemory >= 96) {
				pw.println("maxmem " + this.maxMemory);
			}
			
			if(this.startMemory >= 32) {
				pw.println("alcmem " + this.startMemory);
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
				this.password = this.decodePassword(words[1]);
				break;
			case "forcelang":
				this.forcelang = words[1];
				break;		
			case "maxmem":
				this.maxMemory = Integer.parseInt(words[1]);
				break;
			case "alcmem":
				this.startMemory = Integer.parseInt(words[1]);
				break;
			default:
				System.out.println("Invalid setting \"" + s + "\"");
				break;
			}
		}
	}

	private String encodePassword(String pwd) {
		try {
			return PassEncrypting.encrypt(pwd);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			Logger.getGlobal().warning("Error in encoding password!");
			return pwd;
		}
	}
	
	private String decodePassword(String pwd) {
		try {
			return PassEncrypting.decrypt(pwd);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			Logger.getGlobal().warning("Error in decoding password!");
			return pwd;
		}
	}

}
