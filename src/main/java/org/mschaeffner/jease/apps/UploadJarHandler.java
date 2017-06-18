package org.mschaeffner.jease.apps;

import java.io.InputStream;

import org.mschaeffner.jease.context.JSON;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class UploadJarHandler implements HttpHandler {

	private final AppsRepo appsRepo;

	public UploadJarHandler(AppsRepo appsRepo) {
		this.appsRepo = appsRepo;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) {

		final String appName = exchange.getQueryParameters().get("appName").peek();
		final InputStream inputStream = exchange.getInputStream();

		final App app = appsRepo.upload(appName, inputStream);

		exchange.setStatusCode(201);
		final String responseData = JSON.toJson(app);
		exchange.getResponseSender().send(responseData);
	}

}
