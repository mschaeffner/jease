package org.mschaeffner.jease.context;

import java.io.File;

import org.mschaeffner.jease.apps.CreateAppHandler;
import org.mschaeffner.jease.apps.ListAppsHandler;
import org.mschaeffner.jease.apps.UploadJarHandler;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;

public class Server {

	public Server(File appsDir, int port) {

		final HttpHandler apiHandler = Handlers.routing()

				// get list of all apps
				.get("/apps", new ListAppsHandler(appsDir)) //

				// create a new app
				.post("/apps", new BlockingHandler(new CreateAppHandler(appsDir))) //

				// upload a jar file for an app
				.post("/apps/{appName}/jars", new BlockingHandler(new UploadJarHandler(appsDir))) //

				// fallback handler
				.setFallbackHandler(exchange -> exchange.getResponseSender().send("Hello World")); //

		final HttpHandler pathHandler = Handlers.path() //
				.addPrefixPath("/api", apiHandler);

		final Undertow server = Undertow.builder() //
				.addHttpListener(port, "localhost") //
				.setHandler(pathHandler) //
				.build(); //

		server.start();
	}

}
