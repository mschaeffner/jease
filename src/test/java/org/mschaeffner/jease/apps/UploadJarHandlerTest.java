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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpServerExchange.class)
public class UploadJarHandlerTest {

	@Test
	public void testHandleRequest() throws Exception {

		final Path tempDirPath = Files.createTempDirectory("");
		final File appsDir = tempDirPath.toFile();
		new File(appsDir, "app1").mkdirs();

		final byte[] httpBody = "some-binary-data".getBytes();
		final InputStream httpBodyStream = new ByteArrayInputStream(httpBody);

		final Sender sender = mock(Sender.class);
		final HttpServerExchange exchange = PowerMockito.mock(HttpServerExchange.class);
		when(exchange.getInputStream()).thenReturn(httpBodyStream);
		when(exchange.getResponseSender()).thenReturn(sender);

		final Map<String, Deque<String>> queryParameters = new HashMap<>();
		final Deque<String> param = new ArrayDeque<>();
		param.add("app1");
		queryParameters.put("appName", param);
		when(exchange.getQueryParameters()).thenReturn(queryParameters);

		final UploadJarHandler handler = new UploadJarHandler(appsDir);
		handler.handleRequest(exchange);

		verify(exchange).setStatusCode(201);

		final ArgumentCaptor<String> senderCaptor = ArgumentCaptor.forClass(String.class);
		verify(sender).send(senderCaptor.capture());
		final String jarFileName = senderCaptor.getValue();

		final byte[] fileContent = getJarFileContent(appsDir, "app1", jarFileName);
		assertThat(fileContent, is(httpBody));
	}

	private byte[] getJarFileContent(File appsDir, String appName, String jarFilename) {
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
