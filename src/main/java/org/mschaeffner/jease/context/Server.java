package org.mschaeffner.jease.context;

import javax.inject.Inject;

import org.mschaeffner.jease.apps.AppsRepo;
import org.mschaeffner.jease.apps.CreateAppHandler;
import org.mschaeffner.jease.apps.ListAppsHandler;
import org.mschaeffner.jease.apps.UploadJarHandler;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;

public class Server {

	private final Undertow undertow;

	@Inject
	public Server(ContextConfig contextConfig, //
			AppManager appManager, //
			AppsRepo appsRepo, //
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
				.setFallbackHandler(ResponseCodeHandler.HANDLE_404); //

		final PathHandler pathHandler = Handlers.path() //
				.addPrefixPath("/api", new AuthHandler(apiHandler, contextConfig.getAuthToken()));

		appsRepo.list().forEach(app -> {
			final String path = "/proxy/" + app.getName();
			final LoadBalancingProxyClient loadBalancer = appManager.deployApp(app);
			final ProxyHandler proxyHandler = new ProxyHandler(loadBalancer, 30000, ResponseCodeHandler.HANDLE_404);
			pathHandler.addPrefixPath(path, proxyHandler);
		});

		this.undertow = Undertow.builder() //
				.addHttpListener(contextConfig.getPort(), "localhost") //
				.setHandler(pathHandler) //
				.build(); //
	}

	public void start() {
		undertow.start();
	}

}
