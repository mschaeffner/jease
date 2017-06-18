package org.mschaeffner.jease.apps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.mschaeffner.jease.context.ContextConfig;
import org.mschaeffner.jease.context.JSON;

public class AppsRepo {

	private final ContextConfig contextConfig;

	@Inject
	public AppsRepo(ContextConfig contextConfig) {
		this.contextConfig = contextConfig;
	}

	public App upload(String appName, InputStream inputStream) {

		final String newFileName = getRandomFileName() + ".jar";
		final File appDir = new File(contextConfig.getAppsDir(), appName);
		final File jarFile = new File(appDir, newFileName);

		try {
			Files.copy(inputStream, jarFile.toPath());
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
		}

		// FIXME
		return null;
	}

	public void create(App newApp) {

		final AppConfig newAppConfig = new AppConfig(newApp.getPorts());
		final File newAppDir = new File(contextConfig.getAppsDir(), newApp.getName());
		newAppDir.mkdirs();

		final File jeaseJsonFile = new File(newAppDir, ContextConfig.JEASE_JSON_FILENAME);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(jeaseJsonFile))) {
			final String content = JSON.toJson(newAppConfig);
			writer.write(content);
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
		}
	}

	public List<App> list() {
		final File[] subFiles = contextConfig.getAppsDir().listFiles();
		final List<App> result = Arrays.stream(subFiles) //
				.filter(File::isDirectory) //
				.map(readFileContent) //
				.filter(Objects::nonNull) //
				.collect(Collectors.toList());
		return result;
	}

	private Function<File, App> readFileContent = (file) -> {
		try {
			final File jeaseJsonFile = new File(file, ContextConfig.JEASE_JSON_FILENAME);
			final Path jeaseJsonPath = Paths.get(jeaseJsonFile.getAbsolutePath());
			final byte[] fileContent = Files.readAllBytes(jeaseJsonPath);

			final AppConfig appConfig = JSON.fromJson(new String(fileContent), AppConfig.class);
			final String name = file.getName();

			// TODO retrieve current file name
			final String currentFileName = null;

			final App result = new App(name, appConfig.getPorts(), currentFileName);
			return result;
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
			return null;
		}
	};

	private static String getRandomFileName() {
		final long currentTime = System.currentTimeMillis();
		return String.valueOf(currentTime);
	}

}
