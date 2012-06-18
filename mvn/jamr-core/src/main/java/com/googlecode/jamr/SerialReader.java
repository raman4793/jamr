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

public class SerialReader extends SerialReaderBase
		implements
			SerialPortEventListener {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(SerialReader.class);

	private java.io.InputStream in;

	public SerialReader(java.io.InputStream in) {
		super();
		this.in = in;
		log.trace("init");
	}

	public void serialEvent(SerialPortEvent arg0) {
		int data;
		byte[] buffer = new byte[1024];

		try {
			int len = 0;
			while ((data = in.read()) > -1) {
				if (data == '\n') {
					break;
				}
				buffer[len++] = (byte) data;
			}
			String line = new String(buffer, 0, len);
			doWork(line);
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}
}
