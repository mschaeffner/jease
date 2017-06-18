package org.mschaeffner.jease.apps;

import java.util.List;

public class AppConfig {

	private final List<Integer> ports;

	public AppConfig(List<Integer> ports) {
		this.ports = ports;
	}

	public List<Integer> getPorts() {
		return ports;
	}

}
