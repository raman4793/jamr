package com.googlecode.jamr;

public class Timer implements Runnable {
	private JamrModel jamr;

	Timer(JamrModel jamr) {
		this.jamr = jamr;
	}

	public void run() {
		while (jamr.isRunning()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
}
