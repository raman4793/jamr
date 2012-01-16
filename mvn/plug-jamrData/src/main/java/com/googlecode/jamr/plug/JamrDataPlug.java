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

package com.googlecode.jamr.plug;

public class JamrDataPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(JamrDataPlug.class);

	private JamrDataConfig jdc;

	private org.apache.http.impl.client.DefaultHttpClient httpclient;

	private java.util.Hashtable<String, com.googlecode.jamr.model.EncoderReceiverTransmitterMessage> lastReading;
	private java.util.List<com.googlecode.jamr.model.EncoderReceiverTransmitterMessage> queue;

	public JamrDataPlug() {
		log.trace("init");

		lastReading = new java.util.Hashtable<String, com.googlecode.jamr.model.EncoderReceiverTransmitterMessage>();
		queue = new java.util.Vector<com.googlecode.jamr.model.EncoderReceiverTransmitterMessage>();

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();

		com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();

		java.io.File file = pu.getConfigFile("jamrData");
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(file);
			jdc = (JamrDataConfig) xstream.fromXML(fis);
		} catch (java.io.FileNotFoundException fnfe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			fnfe.printStackTrace(pw);
			log.error(sw.toString());

			// TODO install empty config ??
			jdc = new JamrDataConfig();
		}

		httpclient = new org.apache.http.impl.client.DefaultHttpClient();
	}

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		String serial = ert.getSerial();
		log.warn("received serial: " + serial);

		String url = "http://jamr-data.appspot.com/plug/in";

		String access = jdc.getAccessCode();
		if (access == null) {
			log.error("No Access Code configured");
		}

		com.googlecode.jamr.model.StandardConsumptionMessage old = (com.googlecode.jamr.model.StandardConsumptionMessage) lastReading
				.get(serial);
		if (old != null) {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(old.getDate());
			cal.add(java.util.Calendar.MINUTE, jdc.getMinutesBetween());
			if (ert.getDate().before(cal.getTime())) {
				log.trace("Too Soon - skipping for now");
				return;
			}
		}

		lastReading.put(serial, ert);
		queue.add(ert);

		if (queue.size() < jdc.getItemsInQueue()) {
			log.info("Size of queue: " + queue.size());
			return;
		}

		org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(
				url);

		org.apache.http.message.BasicHeader auth = new org.apache.http.message.BasicHeader(
				"X-JamrDataAccess", access);
		post.addHeader(auth);

		org.apache.http.HttpResponse response = null;
		try {
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(bos);
			out.writeObject(queue);
			out.close();
			byte[] buf = bos.toByteArray();
			post.setEntity(new org.apache.http.entity.ByteArrayEntity(buf));
			response = httpclient.execute(post);
			org.apache.http.StatusLine sl = response.getStatusLine();
			log.info("code: " + sl.getStatusCode());
			if (sl.getStatusCode() == 200) {
				queue = new java.util.Vector();
			}
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		} finally {
			org.apache.http.HttpEntity entity = response.getEntity();
			try {
				org.apache.http.util.EntityUtils.consume(entity);
			} catch (Exception e) {
			}
		}
	}
}
