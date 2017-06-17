package org.mschaeffner.jease.apps;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class UploadJarHandler implements HttpHandler {

	private final File appsDir;

	public UploadJarHandler(File appsDir) {
		this.appsDir = appsDir;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		final String appName = exchange.getQueryParameters().get("appName").peek();
		final InputStream inputStream = exchange.getInputStream();
		final String newFilename = getRandomFilename() + ".jar";

		final File appDir = new File(appsDir, appName);
		final File jarFile = new File(appDir, newFilename);
		Files.copy(inputStream, jarFile.toPath());

		exchange.setStatusCode(201);
		exchange.getResponseSender().send(newFilename);
	}

	private static String getRandomFilename() {
		final long currentTime = System.currentTimeMillis();
		return String.valueOf(currentTime);
	}

}
