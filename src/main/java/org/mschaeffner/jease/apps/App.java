package org.mschaeffner.jease.apps;

import java.util.List;

public class App {

	private final String name;

	private final List<Integer> ports;

	private final String currentFileName;

	public App(String name, List<Integer> ports, String currentFileName) {
		this.name = name;
		this.ports = ports;
		this.currentFileName = currentFileName;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

}
