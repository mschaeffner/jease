package org.mschaeffner.jease.context;

import java.io.File;

public class Main {

	public static final String JEASE_JSON_FILENAME = "jease.json";

	public static void main(String[] args) {
		final int port = getPort();
		final File appsDir = getAppDir();
		new Server(appsDir, port);
	}

	static int getPort() {
		final String portStr = System.getProperty("port");
		try {
			int result = Integer.valueOf(portStr).intValue();
			return result;
		} catch (NumberFormatException e) {
			System.err.println("Port number must be provided as a system property, e.g. -Dport=8080");
			System.exit(-1);
			return -1;
		}
	}

	static File getAppDir() {
		final String appsDirStr = System.getProperty("appsDir");
		if (appsDirStr == null) {
			System.err.println("Apps directory must be provided as a system property, e.g. -DappsDir=/opt/jease/apps");
			System.exit(-1);
		}

		final File appsDir = new File(appsDirStr);
		if (!appsDir.isDirectory()) {
			System.err.println("Path to apps directory is not a directory");
			System.exit(-1);
		}
		return appsDir;
	}

}
