package org.mschaeffner.jease.apps;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mschaeffner.jease.context.JSON;
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

		final AppsRepo appsRepo = mock(AppsRepo.class);
		when(appsRepo.upload("app1", httpBodyStream)).then(new Answer<App>() {

			@Override
			public App answer(InvocationOnMock invocation) throws Throwable {
				final List<Integer> ports = Arrays.asList(1001, 1002);
				return new App("app1", ports, "12345.jar");
			}
		});

		final UploadJarHandler handler = new UploadJarHandler(appsRepo);
		handler.handleRequest(exchange);

		verify(exchange).setStatusCode(201);

		final ArgumentCaptor<String> senderCaptor = ArgumentCaptor.forClass(String.class);
		verify(sender).send(senderCaptor.capture());
		final String json = senderCaptor.getValue();
		final App returnedApp = JSON.fromJson(json, App.class);

		assertThat(returnedApp.getName(), is("app1"));
		assertThat(returnedApp.getCurrentFileName(), is("12345.jar"));
		assertThat(returnedApp.getPorts(), hasSize(2));
		assertThat(returnedApp.getPorts().get(0), is(1001));
		assertThat(returnedApp.getPorts().get(1), is(1002));
	}

}
