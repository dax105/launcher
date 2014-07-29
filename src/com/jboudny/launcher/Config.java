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
	
	private boolean savedCredentials = false;
	
	public boolean hasSavedCredentials() {
		return this.savedCredentials;
	}
	
	public Config(File f) {
		if (f != null && f.exists()) {
			System.out.println("Loading config...");
			load(f);
			if (!username.equals("") && !password.equals("")) {
				savedCredentials = true;
			}
		} else {
			System.out.println("No config found!");
		}
	}
	
	public void load(File f) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			String ln;
			while((ln = br.readLine()) != null) {
				applySetting(ln);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(File f, boolean saveCredentials) {
		try {
			PrintWriter pw = new PrintWriter(f);
			
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
		}
		
	}
	
	public void applySetting(String s) {
		String[] words = s.split(" ");
		try {
			if (words.length == 2) {
				switch(words[0]) {
				case "server": 
					this.server = words[1];
					return;
				case "username":
					this.username = words[1];
					return;
				case "password":
					this.password = words[1];
					return;
				}
			}
		} catch (Exception e) {
			// Do nothing, lulz
		}
		System.out.println("Invalid setting \"" + s + "\"");
	}
	
	
	
}
