package org.mschaeffner.jease.apps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mschaeffner.jease.context.Main;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpServerExchange.class)
public class CreateAppHandlerTest {

	@Test
	public void testHandleRequest() throws Exception {

		final Path tempDirPath = Files.createTempDirectory("");
		final File appsDir = tempDirPath.toFile();

		final String httpBody = "{\"name\":\"app1\",\"ports\":[1001,1002,1003]}";
		final InputStream httpBodyStream = new ByteArrayInputStream(httpBody.getBytes());

		final Sender sender = mock(Sender.class);
		final HttpServerExchange exchange = PowerMockito.mock(HttpServerExchange.class);
		when(exchange.getInputStream()).thenReturn(httpBodyStream);
		when(exchange.getResponseSender()).thenReturn(sender);

		final CreateAppHandler handler = new CreateAppHandler(appsDir);
		handler.handleRequest(exchange);

		final String expectedResult = "{\"name\":\"app1\",\"ports\":[1001,1002,1003]}";
		verify(sender).send(expectedResult);
		verify(exchange).setStatusCode(201);

		final String fileContent = getAppConfigJsonFromFile(appsDir, "app1");
		assertThat(fileContent, is(expectedResult));
	}

	private String getAppConfigJsonFromFile(File appsDir, String appName) {
		try {
			final File appDir = new File(appsDir, appName);
			final File jeaseJsonFile = new File(appDir, Main.JEASE_JSON_FILENAME);
			final Path jeaseJsonPath = Paths.get(jeaseJsonFile.getAbsolutePath());
			final byte[] fileContent = Files.readAllBytes(jeaseJsonPath);
			return new String(fileContent);
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
			return null;
		}
	}

}
