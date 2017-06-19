package org.mschaeffner.jease.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ContextConfig {

	public static final String JEASE_JSON_FILENAME = "jease.json";

	private final int port;

	private final File appsDir;

	private final String authToken;

	public ContextConfig() throws ContextConfigException {

		final String pwd = System.getProperty("user.dir");
		final String propertiesFile = pwd + "/jease.properties";
		final Properties props = new Properties();

		try (InputStream resourceStream = new FileInputStream(new File(propertiesFile));) {
			props.load(resourceStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ContextConfigException(e.getMessage());
		}

		this.port = getPortProperty(props);
		this.appsDir = getAppDirProperty(props);
		this.authToken = getAuthTokenProperty(props);
	}

	public int getPort() {
		return port;
	}

	public File getAppsDir() {
		return appsDir;
	}

	public String getAuthToken() {
		return authToken;
	}

	int getPortProperty(Properties props) throws ContextConfigException {
		final String portStr = props.getProperty("port");
		try {
			int result = Integer.valueOf(portStr).intValue();
			return result;
		} catch (NumberFormatException e) {
			throw new ContextConfigException("Port number must be provided in file jease.properties as 'port: ...'");
		}
	}

	File getAppDirProperty(Properties props) throws ContextConfigException {
		final String appsDirStr = props.getProperty("appsDir");
		if (appsDirStr == null) {
			throw new ContextConfigException(
					"Apps directory must be provided in file jease.properties as 'appsDir: ...'");
		}

		final File appsDir = new File(appsDirStr);
		if (!appsDir.isDirectory()) {
			throw new ContextConfigException("Path to apps directory is not a directory");
		}
		return appsDir;
	}

	private String getAuthTokenProperty(Properties props) throws ContextConfigException {
		final String authToken = props.getProperty("authToken");
		if (authToken == null) {
			throw new ContextConfigException(
					"Auth token must be provided in file jease.properties as 'authToken: ...'");
		}
		return authToken;
	}

}
