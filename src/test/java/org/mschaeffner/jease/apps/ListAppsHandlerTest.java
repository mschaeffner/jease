package org.mschaeffner.jease.apps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mschaeffner.jease.context.Main;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpServerExchange.class)
public class ListAppsHandlerTest {

	private static final Gson GSON = new Gson();

	@Test
	public void testHandleRequest() throws IOException {

		final Path tempDirPath = Files.createTempDirectory("");
		final File appsDir = tempDirPath.toFile();

		final App app1 = new App("app1", Arrays.asList(1001, 1002, 1003));
		createAppFolderWithConfigFile(appsDir, app1);

		final App app2 = new App("app2", Arrays.asList(2001, 2002));
		createAppFolderWithConfigFile(appsDir, app2);

		// create a directory without a jease.json file
		new File(appsDir, "emptyDir").mkdirs();

		// create a random file
		new File(appsDir, "randomFile").createNewFile();

		final Sender sender = mock(Sender.class);
		final HttpServerExchange exchange = PowerMockito.mock(HttpServerExchange.class);
		when(exchange.getResponseSender()).thenReturn(sender);

		final ListAppsHandler handler = new ListAppsHandler(appsDir);
		handler.handleRequest(exchange);

		final String expectedResult = "[{\"name\":\"app1\",\"ports\":[1001,1002,1003]},{\"name\":\"app2\",\"ports\":[2001,2002]}]";
		verify(sender).send(expectedResult);
	}

	private void createAppFolderWithConfigFile(File appsDir, App newApp) {
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
	}

}
