package org.mschaeffner.jease.apps;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
public class CreateAppHandlerTest {

	@Test
	public void testHandleRequest() throws Exception {

		final String httpBody = "{\"name\":\"app1\",\"ports\":[1001,1002,1003]}";
		final InputStream httpBodyStream = new ByteArrayInputStream(httpBody.getBytes());

		final Sender sender = mock(Sender.class);
		final HttpServerExchange exchange = PowerMockito.mock(HttpServerExchange.class);
		when(exchange.getInputStream()).thenReturn(httpBodyStream);
		when(exchange.getResponseSender()).thenReturn(sender);

		final AppsRepo appsRepo = mock(AppsRepo.class);
		final CreateAppHandler handler = new CreateAppHandler(appsRepo);
		handler.handleRequest(exchange);

		final String expectedResult = "{\"name\":\"app1\",\"ports\":[1001,1002,1003]}";
		verify(sender).send(expectedResult);
		verify(exchange).setStatusCode(201);

		final ArgumentCaptor<App> appCaptor = ArgumentCaptor.forClass(App.class);
		verify(appsRepo).create(appCaptor.capture());

		final App capturedApp = appCaptor.getValue();
		assertThat(capturedApp.getName(), is("app1"));
		assertThat(capturedApp.getCurrentFileName(), is(nullValue()));

		assertThat(capturedApp.getPorts(), hasSize(3));
		assertThat(capturedApp.getPorts().get(0), is(1001));
		assertThat(capturedApp.getPorts().get(1), is(1002));
		assertThat(capturedApp.getPorts().get(2), is(1003));
	}

}
