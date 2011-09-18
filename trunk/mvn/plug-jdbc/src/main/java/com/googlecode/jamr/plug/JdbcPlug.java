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

public class JdbcPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(JdbcPlug.class);

	private org.springframework.context.ApplicationContext context;

	public JdbcPlug() {
		log.trace("init");

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();

		context = new org.springframework.context.support.FileSystemXmlApplicationContext(
				"file:" + pu.getConfigPath() + "jdbc");

		// create tables here
		TargetList targetList = (TargetList) context.getBean("TargetList");
		java.util.List targets = targetList.getTargets();
		for (int i = 0; i < targets.size(); i++) {
			try {
				ErtmDAO dao = (ErtmDAO) targets.get(i);
				dao.createTable();
			} catch (Exception e) {
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				e.printStackTrace(pw);
				log.error(sw.toString());
			}
		}
	}

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		log.trace("received");
		String serial = ert.getSerial();
		log.info("serial: " + serial + " reading: " + ert.getReading()
				+ " deltaSeconds: " + ert.getDeltaSeconds() + " deltaReading: "
				+ ert.getDeltaReading());

		TargetList targetList = (TargetList) context.getBean("TargetList");
		java.util.List targets = targetList.getTargets();
		for (int i = 0; i < targets.size(); i++) {
			try {
				ErtmDAO dao = (ErtmDAO) targets.get(i);
				dao.insertErt(ert);

				String details = "";
				Object obj = dao.getDataSource();
				if (obj instanceof org.apache.commons.dbcp.BasicDataSource) {
					org.apache.commons.dbcp.BasicDataSource bds = (org.apache.commons.dbcp.BasicDataSource) obj;
					details = bds.getDriverClassName();
				}
				log.info(details + " Verify: " + dao.verify());
			} catch (Exception e) {
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				e.printStackTrace(pw);
				log.error(sw.toString());
			}
		}
	}
}
