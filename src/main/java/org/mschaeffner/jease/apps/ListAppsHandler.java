package org.mschaeffner.jease.apps;

import java.util.List;

import org.mschaeffner.jease.context.JSON;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ListAppsHandler implements HttpHandler {

	private final AppsRepo appsRepo;

	public ListAppsHandler(AppsRepo appsRepo) {
		this.appsRepo = appsRepo;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) {
		final List<App> apps = appsRepo.list();
		final String responseData = JSON.toJson(apps);
		exchange.getResponseSender().send(responseData);
	}

}
