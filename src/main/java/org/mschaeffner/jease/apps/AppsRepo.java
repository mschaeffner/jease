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

		final App result = convertDirToApp.apply(appDir);
		return result;
	}

	public void create(App newApp) {

		final AppConfig newAppConfig = new AppConfig(newApp.getPorts());
		final File newAppDir = new File(contextConfig.getAppsDir(), newApp.getName());
		newAppDir.mkdirs();

		final File appConfigFile = new File(newAppDir, ContextConfig.APP_CONFIG_JSON_FILENAME);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(appConfigFile))) {
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
				.map(convertDirToApp) //
				.filter(Objects::nonNull) //
				.collect(Collectors.toList());
		return result;
	}

	private Function<File, App> convertDirToApp = (appDir) -> {
		try {
			final File appConfigFile = new File(appDir, ContextConfig.APP_CONFIG_JSON_FILENAME);
			final Path appConfigPath = Paths.get(appConfigFile.getAbsolutePath());
			final byte[] fileContent = Files.readAllBytes(appConfigPath);

			final AppConfig appConfig = JSON.fromJson(new String(fileContent), AppConfig.class);
			final String appName = appDir.getName();

			final File[] appFiles = appDir.listFiles();
			final String currentFileName = Arrays.stream(appFiles) //
					.filter(File::isFile) //
					.map(File::getName) //
					.filter(s -> s.endsWith(".jar")) //
					.sorted((file1, file2) -> file2.compareTo(file1)) //
					.findFirst() //
					.orElse(null);

			final App result = new App(appName, appConfig.getPorts(), currentFileName);
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
