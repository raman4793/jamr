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

public class PlotwattPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(PlotwattPlug.class);

	private PlotwattConfig pc;

	private org.apache.http.impl.client.DefaultHttpClient httpclient;
	private org.apache.http.HttpHost targetHost;
	private org.apache.http.protocol.BasicHttpContext localcontext;

	public PlotwattPlug() {
		log.trace("init");

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();

		// read in settings
		com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();
		java.io.File file = pu.getConfigFile("plotwatt");
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(file);
			pc = (PlotwattConfig) xstream.fromXML(fis);
		} catch (java.io.FileNotFoundException fnfe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			fnfe.printStackTrace(pw);
			log.error(sw.toString());

			// TODO install empty config ??
			pc = new PlotwattConfig();
		}

		targetHost = new org.apache.http.HttpHost("plotwatt.com", 80, "http");

		httpclient = new org.apache.http.impl.client.DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
				new org.apache.http.auth.AuthScope(targetHost.getHostName(),
						targetHost.getPort()),
				new org.apache.http.auth.UsernamePasswordCredentials(pc
						.getHouseCode(), pc.getHouseCode()));

		org.apache.http.client.AuthCache authCache = new org.apache.http.impl.client.BasicAuthCache();
		org.apache.http.impl.auth.BasicScheme basicAuth = new org.apache.http.impl.auth.BasicScheme();
		authCache.put(targetHost, basicAuth);

		localcontext = new org.apache.http.protocol.BasicHttpContext();
		localcontext.setAttribute(
				org.apache.http.client.protocol.ClientContext.AUTH_CACHE,
				authCache);
	}
	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		log.trace("received");
		String serial = ert.getSerial();
		log.info("serial: " + serial + " reading: " + ert.getReading()
				+ " deltaSeconds: " + ert.getDeltaSeconds() + " deltaReading: "
				+ ert.getDeltaReading());

		if (pc.getMeters() != null) {
			String meterid = (String) pc.getMeters().get(serial);
			if (meterid != null) {
				String url = "http://plotwatt.com/api/v2/push_readings";
				log.trace("url: " + url);

				org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(
						url);

				long ts = ert.getDate().getTime() / 1000;
				String csv = meterid + "," + ert.getReading() + "," + ts;
				log.info("csv: " + csv);

				org.apache.http.HttpResponse response = null;
				try {
					org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(
							csv, "text/csv", "utf-8");
					post.setEntity(se);

					response = httpclient.execute(targetHost, post,
							localcontext);
					org.apache.http.StatusLine sl = response.getStatusLine();
					log.info("code: " + sl.getStatusCode());

					if (sl.getStatusCode() != 200) {
						String content = getAsString(response.getEntity());
						log.warn(content);
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
			} else {
				log.warn("Meter not found for serial: " + serial);
			}
		} else {
			log.warn("No meters configured");
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
