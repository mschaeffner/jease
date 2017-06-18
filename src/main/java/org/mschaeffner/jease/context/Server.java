package org.mschaeffner.jease.context;

import javax.inject.Inject;

import org.mschaeffner.jease.apps.CreateAppHandler;
import org.mschaeffner.jease.apps.ListAppsHandler;
import org.mschaeffner.jease.apps.UploadJarHandler;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;

public class Server {

	private final Undertow undertow;

	@Inject
	public Server(ContextConfig contextConfig, //
			ListAppsHandler listAppsHandler, //
			CreateAppHandler createAppHandler, //
			UploadJarHandler uploadJarHandler) {

		final HttpHandler apiHandler = Handlers.routing()

				// get list of all apps
				.get("/apps", listAppsHandler) //

				// create a new app
				.post("/apps", new BlockingHandler(createAppHandler)) //

				// upload a jar file for an app
				.post("/apps/{appName}/jars", new BlockingHandler(uploadJarHandler)) //

				// fallback handler
				.setFallbackHandler(exchange -> exchange.setStatusCode(404)); //

		final HttpHandler pathHandler = Handlers.path() //
				.addPrefixPath("/api", apiHandler);

		this.undertow = Undertow.builder() //
				.addHttpListener(contextConfig.getPort(), "localhost") //
				.setHandler(pathHandler) //
				.build(); //
	}

	public void start() {
		undertow.start();
	}

}
