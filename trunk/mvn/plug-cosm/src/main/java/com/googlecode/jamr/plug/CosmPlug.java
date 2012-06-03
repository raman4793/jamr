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

public class CosmPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CosmPlug.class);

	private CosmConfig pc;
	private java.util.Date lastReading;

	private org.apache.http.impl.client.DefaultHttpClient httpclient;

	public CosmPlug() {
		log.trace("init");

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();

		lastReading = new java.util.Date();

		// read in settings
		com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();
		java.io.File file = pu.getConfigFile("cosm");
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(file);
			pc = (CosmConfig) xstream.fromXML(fis);
		} catch (java.io.FileNotFoundException fnfe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			fnfe.printStackTrace(pw);
			log.error(sw.toString());

			// TODO install empty config ??
			pc = new CosmConfig();
		}

		httpclient = new org.apache.http.impl.client.DefaultHttpClient();
	}

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		log.trace("received");
		String serial = ert.getSerial();
		log.info("serial: " + serial + " reading: " + ert.getReading()
				+ " deltaSeconds: " + ert.getDeltaSeconds() + " deltaReading: "
				+ ert.getDeltaReading());

		if (pc.getSecondsBetween() > 0) {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.add(java.util.Calendar.SECOND, -pc.getSecondsBetween());
			if (cal.getTime().before(lastReading)) {
				log.info("Too soon - skipping seconds between set to: "
						+ pc.getSecondsBetween());
				return;
			}
		}

		if (pc.getStreams() != null) {
			String streamid = (String) pc.getStreams().get(serial);
			if (streamid != null) {
				String feedid = (String) pc.getFeeds().get(streamid);
				if (feedid != null) {
					String key = (String) pc.getKeys().get(feedid);
					if (key != null) {

						String url = "http://api.cosm.com/v2/feeds/" + feedid
								+ "/datastreams/" + streamid + "/datapoints";
						log.trace("url: " + url);

						org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(
								url);
						org.apache.http.message.BasicHeader auth = new org.apache.http.message.BasicHeader(
								"X-ApiKey", key);
						post.addHeader(auth);

						org.joda.time.format.DateTimeFormatter fmt = org.joda.time.format.ISODateTimeFormat
								.dateTime();
						String csv = fmt.print(org.joda.time.LocalDateTime
								.fromDateFields(ert.getDate()))
								+ "," + ert.getReading() + "\n";
						log.debug("csv: " + csv);

						org.apache.http.HttpResponse response = null;
						try {
							org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(
									csv, "text/csv", "utf-8");
							post.setEntity(se);

							response = httpclient.execute(post);
							org.apache.http.StatusLine sl = response
									.getStatusLine();
							log.info("code: " + sl.getStatusCode());

							if (sl.getStatusCode() != 200) {
								String content = getAsString(response
										.getEntity());
								log.warn(content);
							}

							lastReading = new java.util.Date();
						} catch (Exception e) {
							java.io.StringWriter sw = new java.io.StringWriter();
							java.io.PrintWriter pw = new java.io.PrintWriter(sw);
							e.printStackTrace(pw);
							log.error(sw.toString());
						} finally {
							org.apache.http.HttpEntity entity = response
									.getEntity();
							try {
								org.apache.http.util.EntityUtils
										.consume(entity);
							} catch (Exception e) {
							}
						}
					} else {
						log.warn("Key not found for feed: " + feedid);
					}
				} else {
					log.warn("Feed not found for stream: " + streamid);
				}
			} else {
				log.warn("Stream not found for serial: " + serial);
			}
		} else {
			log.warn("No streams configured");
		}
	}
	private String getAsString(org.apache.http.HttpEntity entity) {
		StringBuilder sb = new StringBuilder();
		try {
			java.io.BufferedReader br = new java.io.BufferedReader(
					new java.io.InputStreamReader(entity.getContent()));
			String line = null;

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			br.close();

			org.apache.http.util.EntityUtils.consume(entity);
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error("Stream To String Error: " + sw.toString());
		}

		return (sb.toString());
	}
}
