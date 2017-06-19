package org.mschaeffner.jease.context;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

@Singleton
public class ProcessManager {

	private final Map<String, Process> processMap = new ConcurrentHashMap<>();

	public ProcessManager() {
		System.out.println("Constructr of ProcessManager");
	}

	public void startProcess(String appName, int port, String jarPath) throws IOException {

		System.out.println("Start process " + appName + ":" + port + " " + jarPath);

		final ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "-Dport=" + port, jarPath);
		final Process process = processBuilder.start();
		final String key = getKey(appName, port);
		processMap.put(key, process);
		
		// TODO
		// wait until app responses (do ping and wait for pong)
	}

	public void stopProcess(String appName, int port) {

		System.out.println("Stop process " + appName + ":" + port);

		final String key = getKey(appName, port);
		final Process process = processMap.get(key);
		if (process != null) {

			try {
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// TODO
		// check and wait for process to terminate
	}

	private String getKey(String appName, int port) {
		return appName + "___" + port;
	}

}
