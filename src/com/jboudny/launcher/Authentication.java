package com.jboudny.launcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;


public class Authentication {
	public enum AuthStatus {
		ERROR_BAD, ERROR_OTHER, FINE
	}
	
	private final String AUTHSERVICE_URL = "authTokenServ";

	private User currentUser;

	public Authentication() {
	}

	public String getUserName() {
		if(currentUser == null)
			return "Player";
		
		return currentUser.getUserName();
	}

	public String getToken() {
		if(this.currentUser == null || !this.currentUser.isAuthenticated())
			return null;
		
		return this.currentUser.getToken();
	}
	
	public AuthStatus authenticate(String userName, String password) {
		String par;
		
		try {
			par = "username=" + URLEncoder.encode(userName, "UTF-8") +
			"&password=" + URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return AuthStatus.ERROR_OTHER;
		}
		
		String result = this.excutePost(AUTHSERVICE_URL, par);
		
		if(result == null || result.isEmpty())
			return AuthStatus.ERROR_OTHER;
		
		try {
			return AuthStatus.valueOf(result.trim());
		} catch(Exception e) {
			currentUser = new User(userName, password);
			currentUser.setToken(result.trim());
			currentUser.setAuthenticated();
			return AuthStatus.FINE;
		}
	}
	
	public boolean isAuthenticated() {
		return currentUser.isAuthenticated();
	}
	

	private String excutePost(String url, String params) {
		Logger.getGlobal().info("POST: " + url + ", params " + params);
		return "hash1234token";
	}

	/*
	private String excutePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

*/
}