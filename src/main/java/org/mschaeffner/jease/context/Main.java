package org.mschaeffner.jease.context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class Main {

	public static void main(String[] args) throws ContextConfigException {
		
		final ContextConfig config = new ContextConfig();
		final Module module = new MainModule(config);
		final Injector injector = Guice.createInjector(module);

		final Server server = injector.getInstance(Server.class);
		server.start();
	}

}
