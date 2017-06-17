package org.mschaeffner.jease.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mschaeffner.jease.context.Main;

import com.google.gson.Gson;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ListAppsHandler implements HttpHandler {

	private static final Gson GSON = new Gson();

	private final File appsDir;

	public ListAppsHandler(File appsDir) {
		this.appsDir = appsDir;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) {

		final File[] subFiles = appsDir.listFiles();
		final List<App> apps = Arrays.stream(subFiles) //
				.filter(File::isDirectory) //
				.map(readFileContent) //
				.map(jsonToApp) //
				.filter(Objects::nonNull) //
				.collect(Collectors.toList());

		final String responseData = GSON.toJson(apps);
		exchange.getResponseSender().send(responseData);
	}

	private Function<String, App> jsonToApp = (s) -> GSON.fromJson(s, App.class);

	private Function<File, String> readFileContent = (file) -> {
		try {
			final File jeaseJsonFile = new File(file, Main.JEASE_JSON_FILENAME);
			final Path jeaseJsonPath = Paths.get(jeaseJsonFile.getAbsolutePath());
			final byte[] fileContent = Files.readAllBytes(jeaseJsonPath);
			return new String(fileContent);
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
			return null;
		}
	};

}
