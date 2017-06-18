package org.mschaeffner.jease.context;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {

	private final ContextConfig contextConfig;

	public MainModule(ContextConfig contextConfig) {
		this.contextConfig = contextConfig;
	}

	@Override
	protected void configure() {
		bind(ContextConfig.class).toInstance(contextConfig);
	}

}
