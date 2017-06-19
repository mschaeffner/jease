package org.mschaeffner.jease.context;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class AuthHandler implements HttpHandler {

	private final HttpHandler toWrap;

	private final String authToken;

	public AuthHandler(HttpHandler toWrap, String authToken) {
		this.toWrap = toWrap;
		this.authToken = authToken;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		if (exchange.isInIoThread()) {
			exchange.dispatch(this);
			return;
		}

		final String authHeader = exchange.getRequestHeaders().getFirst("auth");
		final boolean authenticated = (authHeader != null && authHeader.equals(authToken));

		if (authenticated) {
			if (!exchange.isComplete()) {
				toWrap.handleRequest(exchange);
			}
		} else {
			exchange.setStatusCode(401);
			exchange.endExchange();
		}

	}

}
