package org.mschaeffner.jease.context;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mschaeffner.jease.apps.App;

import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;

@Singleton
public class AppManager {

	private final ContextConfig contextConfig;

	private final ProcessManager processManager;

	private final Map<String, LoadBalancingProxyClient> proxyClients = new HashMap<>();

	@Inject
	public AppManager(ContextConfig contextConfig, ProcessManager processManager) {
		this.contextConfig = contextConfig;
		this.processManager = processManager;
	}

	public synchronized LoadBalancingProxyClient deployApp(App app) {
		final LoadBalancingProxyClient loadBalancer = getLoadBalancer(app.getName());

		app.getPorts().forEach(port -> {
			try {

				final URI uri = new URI("http://localhost:" + port);
				loadBalancer.removeHost(uri);
				processManager.stopProcess(app.getName(), port);

				final File appDir = new File(contextConfig.getAppsDir(), app.getName());
				final File jarFile = new File(appDir, app.getCurrentFileName());
				processManager.startProcess(app.getName(), port, jarFile.getAbsolutePath());
				loadBalancer.addHost(uri);

			} catch (URISyntaxException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		return loadBalancer;
	}

	private LoadBalancingProxyClient getLoadBalancer(String appName) {
		LoadBalancingProxyClient loadBalancer = proxyClients.get(appName);
		if (loadBalancer == null) {
			loadBalancer = new LoadBalancingProxyClient() //
					.setConnectionsPerThread(20);
			proxyClients.put(appName, loadBalancer);
		}
		return loadBalancer;
	}

}
