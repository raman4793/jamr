package com.googlecode.jamr;

public class JamrModel extends java.util.Observable {

	private boolean running = true;
	private org.eclipse.jetty.server.Server server;

	public boolean isRunning() {
		return this.running;
	}

	public void setRunning(boolean r) {
		this.running = r;
	}

	public org.eclipse.jetty.server.Server getServer() {
		return server;
	}

	public void setServer(org.eclipse.jetty.server.Server s) {
		this.server = s;
	}
}
