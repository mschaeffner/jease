package org.mschaeffner.jease.apps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import org.mschaeffner.jease.context.JSON;

import com.google.common.io.CharStreams;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class CreateAppHandler implements HttpHandler {

	private final AppsRepo appsRepo;

	@Inject
	public CreateAppHandler(AppsRepo appsRepo) {
		this.appsRepo = appsRepo;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) {
		final InputStream in = exchange.getInputStream();
		final String newAppJson;

		try {
			newAppJson = CharStreams.toString(new InputStreamReader(in, "UTF-8"));
		} catch (IOException e) {
			// TODO log exception properly
			System.err.println(e.getMessage());
			exchange.setStatusCode(400);
			return;
		}

		final App newApp = JSON.fromJson(newAppJson, App.class);
		appsRepo.create(newApp);

		exchange.setStatusCode(201);
		final String result = JSON.toJson(newApp);
		exchange.getResponseSender().send(result);
	}

}
