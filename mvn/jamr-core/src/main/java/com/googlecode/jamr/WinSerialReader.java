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

import com.engidea.comm.*;

public class WinSerialReader extends SerialReaderBase
		implements
			SerialPortEventListener,
			Runnable {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(WinSerialReader.class);

	private boolean running;

	private SerialPort serport;

	public WinSerialReader(SerialPort sp) {
		super();
		this.serport = sp;
		log.warn("WinSerialReader init");
	}

	public void setRunning(boolean r) {
		log.warn("WinSerialReader setRunning");
		this.running = r;
	}

	public void serialEvent(SerialPortEvent arg0) {
		log.info("Event: " + arg0);
	}

	public void run() {
		log.warn("WinSerialReader run");
		String line = "";
		try {
			byte[] buff = new byte[1];
			while (serport.read(buff, 0, buff.length) > 0) {
				if (buff[0] == '\n') {
					doWork(line);
					line = "";
				} else {
					line = line + new String(buff);
				}
			}
			log.info("Leaving while");
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}
}
