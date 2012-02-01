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

public class CsvPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CsvPlug.class);

	private String csvFile;

	public CsvPlug() {
		log.trace("init");

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();
		csvFile = pu.getConfigPath() + "output.csv";
	}

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		String serial = ert.getSerial();
		log.trace("received serial: " + serial);

		try {
			java.io.File csv = new java.io.File(csvFile);
			if (!csv.exists()) {
				java.io.FileWriter header = new java.io.FileWriter(csv);
				header.append("\"TIMESTAMP\"");
				header.append(",");
				header.append("\"SERIAL\"");
				header.append(",");
				header.append("\"READING\"");
				header.append(",");
				header.append("\"FREQUENCY\"");
				header.append(",");
				header.append("\"SIGNAL\"");
				header.append("\n");

				header.flush();
				header.close();
			}

			java.io.FileWriter writer = new java.io.FileWriter(csv, true);
			writer.append("\"" + ert.getDate().getTime() + "\"");
			writer.append(",");
			writer.append("\"" + serial + "\"");
			writer.append(",");
			writer.append("\"" + ert.getReading() + "\"");
			writer.append(",");
			writer.append("\"" + ert.getFrequency() + "\"");
			writer.append(",");
			writer.append("\"" + ert.getSignalStrength() + "\"");
			writer.append("\n");

			writer.flush();
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}

	}
}
