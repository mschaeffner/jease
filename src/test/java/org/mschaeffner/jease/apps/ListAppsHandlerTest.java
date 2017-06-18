package org.mschaeffner.jease.apps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpServerExchange.class)
public class ListAppsHandlerTest {

	@Test
	public void testHandleRequest() throws IOException {

		final App app1 = new App("app1", Arrays.asList(1001, 1002, 1003), "abc.jar");
		final App app2 = new App("app2", Arrays.asList(2001, 2002), null);
		final App app3 = new App("app3", Arrays.asList(2001, 2002), "12345.jar");

		final List<App> apps = Arrays.asList(app1, app2, app3);
		final AppsRepo appsRepo = mock(AppsRepo.class);
		when(appsRepo.list()).thenReturn(apps);

		final Sender sender = mock(Sender.class);
		final HttpServerExchange exchange = PowerMockito.mock(HttpServerExchange.class);
		when(exchange.getResponseSender()).thenReturn(sender);

		final ListAppsHandler handler = new ListAppsHandler(appsRepo);
		handler.handleRequest(exchange);

		final String expectedResult = "[{\"name\":\"app1\",\"ports\":[1001,1002,1003],\"currentFileName\":\"abc.jar\"},{\"name\":\"app2\",\"ports\":[2001,2002]},{\"name\":\"app3\",\"ports\":[2001,2002],\"currentFileName\":\"12345.jar\"}]";
		verify(sender).send(expectedResult);
	}

}
