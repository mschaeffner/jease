package org.mschaeffner.jease.context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContextConfig {

	public static final String APP_CONFIG_JSON_FILENAME = "app.config.json";

	public static final String GLOBAL_CONFIG_JSON_FILENAME = "global.config.json";

	private final int port;

	private final String authToken;

	private final File appsDir;

	public ContextConfig() throws ContextConfigException {

		final String pwd = System.getProperty("user.dir");
		final File globalConfigFile = new File(pwd + "/" + GLOBAL_CONFIG_JSON_FILENAME);
		final Path globalConfigPath = Paths.get(globalConfigFile.getAbsolutePath());

		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(globalConfigPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ContextConfigException(e.getMessage());
		}

		final GlobalConfig globalConfig = JSON.fromJson(new String(fileContent), GlobalConfig.class);

		this.port = checkPort(globalConfig.getPort());
		this.authToken = checkAuthToken(globalConfig.getAuthToken());
		this.appsDir = checkAppsDir(globalConfig.getAppsDir());
	}

	public int getPort() {
		return port;
	}

	public String getAuthToken() {
		return authToken;
	}

	public File getAppsDir() {
		return appsDir;
	}

	private int checkPort(int port) throws ContextConfigException {
		if (port <= 0) {
			throw new ContextConfigException("Port number must be provided in file global.config.json as 'port: ...'");
		}
		return port;
	}

	private String checkAuthToken(String authToken) throws ContextConfigException {
		if (authToken == null) {
			throw new ContextConfigException(
					"Auth token must be provided in file global.config.json as 'authToken: ...'");
		}
		return authToken;
	}

	private File checkAppsDir(String appsDirStr) throws ContextConfigException {
		if (appsDirStr == null) {
			throw new ContextConfigException(
					"Apps directory must be provided in file global.config.json as 'appsDir: ...'");
		}

		final File appsDir = new File(appsDirStr);
		if (!appsDir.isDirectory()) {
			throw new ContextConfigException("Path to apps directory is not a directory");
		}
		return appsDir;
	}

}
