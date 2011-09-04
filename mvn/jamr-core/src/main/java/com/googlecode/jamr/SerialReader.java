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

public class SerialReader implements SerialPortEventListener {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(SerialReader.class);

	private java.util.concurrent.BlockingQueue<String> blockingQueue;

	private java.util.Hashtable<String, com.googlecode.jamr.model.EncoderReceiverTransmitterMessage> lastReading;

	private java.io.InputStream in;
	private byte[] buffer = new byte[1024];

	private java.util.regex.Pattern pattern = java.util.regex.Pattern
			.compile("^\\$(.*)\\*(.*)\\r$");

	private JamrConfig jc;

	public SerialReader(java.io.InputStream in) {
		log.trace("init");
		this.in = in;
		blockingQueue = new java.util.concurrent.LinkedBlockingQueue<String>();
		lastReading = new java.util.Hashtable<String, com.googlecode.jamr.model.EncoderReceiverTransmitterMessage>();

		java.util.Properties properties = System.getProperties();
		String home = properties.getProperty("user.home");

		com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();
		java.io.File file = new java.io.File(home + "/.jamr/config");
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(file);
			jc = (JamrConfig) xstream.fromXML(fis);
			log.trace("JamrConfig loaded");
		} catch (java.io.FileNotFoundException fnfe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			fnfe.printStackTrace(pw);
			log.error(sw.toString());

			// TODO install an empty config ??
			jc = new JamrConfig();
		}
	}

	public java.util.Hashtable<String, com.googlecode.jamr.model.EncoderReceiverTransmitterMessage> getLatestReadings() {
		return lastReading;
	}

	public void serialEvent(SerialPortEvent arg0) {
		int data;

		try {
			int len = 0;
			while ((data = in.read()) > -1) {
				if (data == '\n') {
					break;
				}
				buffer[len++] = (byte) data;
			}
			String line = new String(buffer, 0, len);
			//log.trace( "-" + line + "-" );
			java.util.regex.Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				String message = matcher.group(1);
				String checksum = matcher.group(2);
				log.debug("Match: " + message + " checksum: " + checksum);

				if (valid(message, checksum)) {
					OutletService os = OutletService.getInstance();
					String parts[] = message.split(",");
					String messageType = parts[0];

					if ((messageType.equals("UMSCM"))
							|| (messageType.equals("UMSCP"))) {
						log.trace("UMS Message");
						com.googlecode.jamr.model.StandardConsumptionMessage scm = new com.googlecode.jamr.model.StandardConsumptionMessage(
								parts);
						com.googlecode.jamr.model.StandardConsumptionMessage customMessage = null;

						String serial = scm.getSerial();

						String functionName = null;
						java.util.Hashtable opers = jc.getOperators();
						if (opers != null) {
							log.trace("Operators != null");
							functionName = (String) opers.get(serial);
						} else {
							log.trace("Operators is null");
						}

						String function = null;
						if (functionName != null) {
							function = (String) jc.getFunctions().get(
									functionName);
							if (function != null) {
								String custom = evaluate(function, scm
										.getReading());
								log.trace("Custom: " + custom);
								if (!custom.equals("")) {
									customMessage = new com.googlecode.jamr.model.StandardConsumptionMessage(
											parts);
									customMessage.setSerial(customMessage
											.getSerial()
											+ "-" + functionName);
									customMessage
											.setReading(new java.math.BigDecimal(
													custom));
								}
							}
						}

						Object test = lastReading.get(serial);
						if (test == null) {
							log.trace("Nothing in history - sending");
							os.received(scm);
							lastReading.put(serial, scm);
							if (customMessage != null) {
								log
										.trace("Nothing in history - sending custom");
								os.received(customMessage);
							}
						} else {
							com.googlecode.jamr.model.StandardConsumptionMessage old = (com.googlecode.jamr.model.StandardConsumptionMessage) test;

							//getTime returns miliseconds
							scm.setDeltaReading(scm.getReading().subtract(
									old.getReading()));
							scm.setDeltaSeconds((scm.getDate().getTime() - old
									.getDate().getTime()) / 1000);

							if (customMessage != null) {
								String custom = evaluate(function, old
										.getReading());
								customMessage
										.setDeltaReading(customMessage
												.getReading()
												.subtract(
														new java.math.BigDecimal(
																custom)));
								customMessage.setDeltaSeconds((customMessage
										.getDate().getTime() - old.getDate()
										.getTime()) / 1000);
							}

							if (!old.getReading().equals(scm.getReading())) {
								log
										.trace("This reading does not match old reading - sending");
								os.received(scm);
								lastReading.put(serial, scm);
								if (customMessage != null) {
									log
											.trace("This reading does not match old reading - sending custom");
									os.received(customMessage);
								}
							} else {
								log.trace("Nothing has changed");
								java.util.Calendar cal = java.util.Calendar
										.getInstance();
								cal.add(java.util.Calendar.MINUTE, -5);
								if (cal.getTime().after(scm.getDate())) {
									// 5 minutes seem like a good period
									log.trace("Nothing has changed - sending");
									os.received(scm);
									lastReading.put(serial, scm);
									if (customMessage != null) {
										log
												.trace("Nothing has changed - sending custom");
										os.received(customMessage);
									}
								}
							}
						}
					} else if ((messageType.equals("UMIDM"))
							|| (messageType.equals("UMIDP"))) {
						log.warn("UMIDM and UMIDP not yet implemented");
					} else {
						// This must be a command response
						log.trace("Command Response: " + message);
						try {
							blockingQueue.put(message);
						} catch (java.lang.InterruptedException ie) {
							java.io.StringWriter sw = new java.io.StringWriter();
							java.io.PrintWriter pw = new java.io.PrintWriter(sw);
							ie.printStackTrace(pw);
							log.error(sw.toString());
						}
					}
				} else {
					log.error("Checksum error: " + line);
				}
			} else {
				log.error("No match: " + line);
			}
		} catch (java.io.IOException ioe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			ioe.printStackTrace(pw);
			log.error(sw.toString());
		}
	}

	private String evaluate(String s, java.math.BigDecimal reading) {
		org.mozilla.javascript.Context cx = org.mozilla.javascript.Context
				.enter();
		try {
			org.mozilla.javascript.Scriptable scope = cx.initStandardObjects();

			cx.evaluateString(scope, s, "<cmd>", 1, null);

			Object fObj = scope.get("f", scope);
			if (!(fObj instanceof org.mozilla.javascript.Function)) {
				log.error("f is undefined or not a function.");
			} else {
				Object functionArgs[] = {reading};
				org.mozilla.javascript.Function f = (org.mozilla.javascript.Function) fObj;
				Object result = f.call(cx, scope, scope, functionArgs);
				return (org.mozilla.javascript.Context.toString(result));
			}
		} finally {
			org.mozilla.javascript.Context.exit();
		}

		return ("");
	}

	public String take() {
		String message = "";
		try {
			message = blockingQueue.take();
		} catch (java.lang.InterruptedException ie) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			ie.printStackTrace(pw);
			log.error(sw.toString());
		}
		return message;
	}

	private boolean valid(String value, String checksum) {
		int calculatedChecksum = 0;

		try {
			byte[] data = value.getBytes("UTF-8");
			calculatedChecksum = data[0] ^ data[1];
			for (int b = 2; b < data.length; ++b) {
				calculatedChecksum = calculatedChecksum ^ data[b];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String calcSumString = Integer.toHexString(calculatedChecksum);
		log.trace("Calculated checksum: " + calcSumString);

		if (calcSumString.equalsIgnoreCase(checksum)) {
			return true;
		}

		return false;
	}
}
