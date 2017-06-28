package org.mschaeffner.jease.context;

public class GlobalConfig {

	private final int port;

	private final String appsDir;

	private final String authToken;

	public GlobalConfig(int port, String appsDir, String authToken) {
		this.port = port;
		this.appsDir = appsDir;
		this.authToken = authToken;
	}

	public int getPort() {
		return port;
	}

	public String getAppsDir() {
		return appsDir;
	}

	public String getAuthToken() {
		return authToken;
	}

}
