package org.mschaeffner.jease.apps;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mschaeffner.jease.context.ContextConfig;
import org.mschaeffner.jease.context.JSON;

public class AppsRepoTest {

	@Test
	public void testList() throws IOException {
		final Path tempDirPath = Files.createTempDirectory("");
		final File appsDir = tempDirPath.toFile();

		final App app1 = new App("app1", Arrays.asList(1001, 1002, 1003), "abc.jar");
		createAppFolderWithConfigFile(appsDir, app1);

		final App app2 = new App("app2", Arrays.asList(2001, 2002), null);
		createAppFolderWithConfigFile(appsDir, app2);

		final App app3 = new App("app3", Arrays.asList(3001, 3002), "12345.jar");
		createAppFolderWithConfigFile(appsDir, app3);

		// create a directory without a jease.json file
		new File(appsDir, "emptyDir").mkdirs();

		// create a random file
		new File(appsDir, "randomFile").createNewFile();

		final ContextConfig contextConfig = mock(ContextConfig.class);
		when(contextConfig.getAppsDir()).thenReturn(appsDir);

		final AppsRepo appsRepo = new AppsRepo(contextConfig);
		final List<App> result = appsRepo.list();

		assertThat(result, hasSize(3));

		assertThat(result.get(0).getName(), is("app1"));
		// FIXME
		//assertThat(result.get(0).getCurrentFileName(), is("abc.jar"));
		assertThat(result.get(0).getPorts(), is(Arrays.asList(1001, 1002, 1003)));

		assertThat(result.get(1).getName(), is("app2"));
		// FIXME
		//assertThat(result.get(1).getCurrentFileName(), is(nullValue()));
		assertThat(result.get(1).getPorts(), is(Arrays.asList(2001, 2002)));

		assertThat(result.get(2).getName(), is("app3"));
		// FIXME
		//assertThat(result.get(2).getCurrentFileName(), is("12345.jar"));
		assertThat(result.get(2).getPorts(), is(Arrays.asList(3001, 3002)));
	}

	private void createAppFolderWithConfigFile(File appsDir, App newApp) {
		final File newAppDir = new File(appsDir, newApp.getName());
		newAppDir.mkdirs();

		final File jeaseJsonFile = new File(newAppDir, ContextConfig.JEASE_JSON_FILENAME);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(jeaseJsonFile))) {
			final AppConfig appConfig = new AppConfig(newApp.getPorts());
			final String content = JSON.toJson(appConfig);
			writer.write(content);
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
		}
		
		if(newApp.getCurrentFileName() != null) {
			final File currentJarFile = new File(newAppDir, newApp.getCurrentFileName());
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentJarFile))) {
				writer.write("");
			} catch (IOException e) {
				// TODO log exception properly
				System.err.println(e.getMessage());
			}
		}
		
	}

	@Test
	public void testCreate() throws Exception {

		final Path tempDirPath = Files.createTempDirectory("");
		final File appsDir = tempDirPath.toFile();

		final App newApp = new App("app1", Arrays.asList(1001, 1002, 1003), null);

		final ContextConfig contextConfig = mock(ContextConfig.class);
		when(contextConfig.getAppsDir()).thenReturn(appsDir);

		final AppsRepo appsRepo = new AppsRepo(contextConfig);
		appsRepo.create(newApp);

		final byte[] expectedResult = "{\"ports\":[1001,1002,1003]}".getBytes();
		final byte[] fileContent = getFileContent(appsDir, "app1", ContextConfig.JEASE_JSON_FILENAME);
		assertThat(fileContent, is(expectedResult));
	}

	@Test
	public void testHandleRequest() throws Exception {

		final Path tempDirPath = Files.createTempDirectory("");
		final File appsDir = tempDirPath.toFile();
		new File(appsDir, "app1").mkdirs();

		final byte[] httpBody = "some-binary-data".getBytes();
		final InputStream inputStream = new ByteArrayInputStream(httpBody);

		final ContextConfig contextConfig = mock(ContextConfig.class);
		when(contextConfig.getAppsDir()).thenReturn(appsDir);

		final AppsRepo appsRepo = new AppsRepo(contextConfig);
		final App returnedApp = appsRepo.upload("app1", inputStream);
		//FIXME
		//final byte[] fileContent = getFileContent(appsDir, "app1", returnedApp.getCurrentFileName());
		//assertThat(fileContent, is(httpBody));
	}

	private byte[] getFileContent(File appsDir, String appName, String jarFilename) {
		try {
			final File appDir = new File(appsDir, appName);
			final File jarFile = new File(appDir, jarFilename);
			final Path jarFilePath = Paths.get(jarFile.getAbsolutePath());
			final byte[] fileContent = Files.readAllBytes(jarFilePath);
			return fileContent;
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
			return null;
		}
	}

}
