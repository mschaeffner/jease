package org.mschaeffner.jease.apps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mschaeffner.jease.context.Main;

import com.google.gson.Gson;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class CreateAppHandler implements HttpHandler {

	private static final Gson GSON = new Gson();

	private final File appsDir;

	public CreateAppHandler(File appsDir) {
		this.appsDir = appsDir;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		final InputStream in = exchange.getInputStream();
		final Reader reader = new InputStreamReader(in, "UTF-8");
		final App newApp = GSON.fromJson(reader, App.class);

		final File newAppDir = new File(appsDir, newApp.getName());
		newAppDir.mkdirs();

		final File jeaseJsonFile = new File(newAppDir, Main.JEASE_JSON_FILENAME);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(jeaseJsonFile))) {
			final String content = GSON.toJson(newApp);
			writer.write(content);
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
		}

		exchange.setStatusCode(201);
		final String result = GSON.toJson(newApp);
		exchange.getResponseSender().send(result);
	}

}
