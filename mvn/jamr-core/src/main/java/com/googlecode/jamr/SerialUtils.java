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

public class SerialUtils {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(SerialUtils.class);

	private static SerialUtils utils;

	private CommPort commPort;
	private Thread serialWriter;

	private SerialReaderBase sr;
	private SerialWriter sw;

	private String os;

	private SerialUtils() {
		log.debug("init");
		os = System.getProperty("os.name");
		log.debug("running on: " + os);
	}

	public static synchronized SerialUtils getInstance() {
		log.debug("getInstance");
		if (utils == null) {
			utils = new SerialUtils();
		}
		return utils;
	}

	public java.util.List getPorts() {
		java.util.List ports = new java.util.Vector();
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			String port = portIdentifier.getName() + " - "
					+ getPortTypeName(portIdentifier.getPortType());
			ports.add(port);
		}
		return ports;
	}

	public boolean connect(String portName) throws Exception {

		if (os.equals("Windows 7")) {
			log.debug("Going to use Winjcom");
			com.engidea.comm.CommPortIdentifier portIdentifier = new com.engidea.win32jcom.WinjcomIdentifier(
					0);
			com.engidea.comm.CommPort aPort = portIdentifier
					.getCommPort(portName);
			aPort.open();
			com.engidea.comm.SerialPort serport = (com.engidea.comm.SerialPort) aPort;

			sr = new WinSerialReader(serport);

			return true;
		}

		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			log.error("port " + portIdentifier.getName()
					+ " is currently in use");
			return false;
		} else {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				java.io.InputStream in = serialPort.getInputStream();
				java.io.OutputStream out = serialPort.getOutputStream();

				sw = new SerialWriter(out);
				sw.setRunning(true);
				serialWriter = new Thread(sw);
				serialWriter.start();

				sr = new SerialReader(in);
				serialPort.addEventListener((SerialReader) sr);
				serialPort.notifyOnDataAvailable(true);
			} else {
				log.error(commPort + " is not handled here");
				return false;
			}
		}
		return true;
	}

	public String sendRawData(String data) {
		sw.send(data + "\r\n");
		return (sr.take());
	}

	public boolean setFullOutputOn() {
		sw.send("FULL ON\r\n");
		return (sr.take().equalsIgnoreCase("UMMSG,OK"));
	}

	public boolean setFullOutputOff() {
		sw.send("FULL OFF\r\n");
		return (sr.take().equalsIgnoreCase("UMMSG,OK"));
	}

	public String getSerialNumber() {
		sw.send("MYID\r\n");
		return (sr.take());
	}

	public String getSpectrumAnalysis() {
		sw.send("SSCN\r\n");
		return (sr.take());
	}

	public String getVersion() {
		sw.send("VRSN\r\n");
		return (sr.take());
	}

	public java.util.Hashtable<String, com.googlecode.jamr.model.EncoderReceiverTransmitterMessage> getLatestReadings() {
		return (sr.getLatestReadings());
	}

	public void disconnect() {
		log.trace("Closing");
		sw.setRunning(false);
		commPort.close();
	}

	private String getPortTypeName(int portType) {
		switch (portType) {
			case CommPortIdentifier.PORT_I2C :
				return "I2C";
			case CommPortIdentifier.PORT_PARALLEL :
				return "Parallel";
			case CommPortIdentifier.PORT_RAW :
				return "Raw";
			case CommPortIdentifier.PORT_RS485 :
				return "RS485";
			case CommPortIdentifier.PORT_SERIAL :
				return "Serial";
			default :
				return "unknown type";
		}
	}
}
