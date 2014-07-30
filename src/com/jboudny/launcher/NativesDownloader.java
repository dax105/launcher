package com.jboudny.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.jboudny.launcher.localization.LocalizationHelper;

public class NativesDownloader {
	File nativesFolder;
	File downloadedFile;
	private String windowsNativesUrl;
	private String macNativesUrl;
	private String linuxNativesUrl;
	
	public NativesDownloader(File nativesFolder, String server) throws IllegalArgumentException {
		this.nativesFolder = nativesFolder;
		if(!nativesFolder.exists()) {
			nativesFolder.mkdir();
		} else {
			OSUtils.deleteDirectory(nativesFolder);
			nativesFolder.mkdir();
		}
		
		if(!nativesFolder.isDirectory()) {
			throw new IllegalArgumentException("nativesFolder must be directory!");
		}
		
		this.windowsNativesUrl = server + "natives/windows.zip";
		this.macNativesUrl = server + "natives/osx.zip";
		this.linuxNativesUrl = server + "natives/linux.zip";
	}
	
	public boolean downloadNatives(JProgressBar progress) {
		boolean result = false;
		this.downloadedFile = new File(nativesFolder, "download.zip");
		
		if(OSUtils.isWindows()) {
			result = download(windowsNativesUrl, downloadedFile, progress);
		} else if (OSUtils.isLinux()) {
			result = download(linuxNativesUrl, downloadedFile, progress);
		} else if (OSUtils.isMac()) {
			result = download(macNativesUrl, downloadedFile, progress);
		}
		
		return result;
	}
	
	public void unpackNatives() throws ZipException {
		ZipFile z = new ZipFile(this.downloadedFile);	
		z.extractAll(this.nativesFolder.getAbsolutePath());
		
		this.downloadedFile.deleteOnExit();
	}
	
	private boolean download(String url, File f, JProgressBar pb) {
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

			File downloadFile = new File(f.getAbsolutePath() + "z");
			FileOutputStream fos = new FileOutputStream(downloadFile);
			BufferedInputStream in = null;
			
			int val = 0;
			
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
						LocalizationHelper.getBestLocalization().errorDownloadingNatives() + ": " + e,
						JOptionPane.ERROR_MESSAGE);
			}

			if (f.exists()) {
				f.delete();
			}

			downloadFile.renameTo(f);

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
