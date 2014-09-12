package com.jboudny.launcher;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JarLauncher {
	private String jarPath;
	private Map<String, String> vmArgs;
	private String appArgs = null;
	private String nativesRelativeDir = null;
	
	public JarLauncher(String jarPath) {
		this.jarPath = jarPath;
		this.vmArgs = new HashMap<>();
	}
	
	public void setAppArgs(String arg) {
		this.appArgs = arg;
	}
	
	public void setMaxMemory(int mbytes) {
		this.vmArgs.put("maxmemory", "-Xmx" + mbytes + "M");
	}
	
	public void setMinMemory(int mbytes) {
		this.vmArgs.put("minmemory", "-Xms" + mbytes + "M");
	}
	
	public void setNativesDir(String directory) {
		this.nativesRelativeDir = directory;
	}

	public Process runJar(File workingDirectory) {
		this.setNatives();
		
		StringBuilder cmdB = new StringBuilder();
		cmdB.append(OSUtils.getJVMPath());
		cmdB.append(' ');
		cmdB.append(this.getArgsString(this.vmArgs.values()));
		cmdB.append("-jar ");
		cmdB.append("\"" + this.jarPath + "\"");
		cmdB.append(' ');
		cmdB.append(this.appArgs);
		
		System.out.println(cmdB.toString());
		
		try {
			return Runtime.getRuntime().exec(cmdB.toString(), null, workingDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getAppArgs() {
		return this.appArgs;
	}
	
	private void setNatives(){
		if(nativesRelativeDir != null) {
			vmArgs.put("natives", "-Djava.library.path=" + nativesRelativeDir);
		}
	}
	
	private String getArgsString(Collection<String> argsList) {
		StringBuilder b = new StringBuilder();
		for(String s : argsList) {
			b.append(s);
			b.append(' ');
		}
		
		return b.toString();
	}
}
