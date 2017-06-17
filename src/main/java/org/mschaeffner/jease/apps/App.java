package org.mschaeffner.jease.apps;

import java.util.List;

public class App {

	private final String name;

	private final List<Integer> ports;

	public App(String name, List<Integer> ports) {
		this.name = name;
		this.ports = ports;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getPorts() {
		return ports;
	}

}
