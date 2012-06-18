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
			SerialPortEventListener {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(WinSerialReader.class);

	private SerialPort serport;

	public WinSerialReader(SerialPort sp) {
		super();
		this.serport = sp;
		log.trace("init");
	}

	public void serialEvent(SerialPortEvent arg0) {

		try {
			byte[] buff = new byte[1];
			while (serport.read(buff, 0, buff.length) > 0) {
				String line = new String(buffer);
				doWork(line);
			}
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}
}
