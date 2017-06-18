package org.mschaeffner.jease.context;

import java.io.File;

public class ContextConfig {

	public static final String JEASE_JSON_FILENAME = "jease.json";

	private final int port;

	private final File appsDir;

	public ContextConfig() throws ContextConfigException {
		this.port = getPortProperty();
		this.appsDir = getAppDirProperty();
	}

	public int getPort() {
		return port;
	}

	public File getAppsDir() {
		return appsDir;
	}

	int getPortProperty() throws ContextConfigException {
		final String portStr = System.getProperty("port");
		try {
			int result = Integer.valueOf(portStr).intValue();
			return result;
		} catch (NumberFormatException e) {
			throw new ContextConfigException("Port number must be provided as a system property, e.g. -Dport=8080");
		}
	}

	File getAppDirProperty() throws ContextConfigException {
		final String appsDirStr = System.getProperty("appsDir");
		if (appsDirStr == null) {
			throw new ContextConfigException(
					"Apps directory must be provided as a system property, e.g. -DappsDir=/opt/jease/apps");
		}

		final File appsDir = new File(appsDirStr);
		if (!appsDir.isDirectory()) {
			throw new ContextConfigException("Path to apps directory is not a directory");
		}
		return appsDir;
	}

}
