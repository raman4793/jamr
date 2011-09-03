/**
 * Copyright (C) 2011 Stephen More
 *
 * This file is part of jamr.
 *
 * jamr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jamr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jamr.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.jamr;

import gnu.io.*;

public class SerialWriter implements Runnable {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(SerialWriter.class);

	private java.io.OutputStream out;

	private boolean running;

	public SerialWriter(java.io.OutputStream out) {
		log.trace("init");
		this.out = out;
	}

	public void setRunning(boolean r) {
		this.running = r;
	}

	public void send(String command) {
		try {
			this.out.write(command.getBytes());
		} catch (java.io.IOException ioe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			ioe.printStackTrace(pw);
			log.error(sw.toString());
		}
	}

	public void run() {
		while (running) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (Exception e) {
			}
		}

		log.trace("Exiting");
	}
}
