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

public class OutletService {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(OutletService.class);

	private static OutletService service;
	private java.util.ServiceLoader<com.googlecode.jamr.spi.Outlet> loader;

	private OutletService() {
		loader = java.util.ServiceLoader
				.load(com.googlecode.jamr.spi.Outlet.class);
	}

	public static synchronized OutletService getInstance() {
		log.debug("getInstance");
		if (service == null) {
			service = new OutletService();
		}
		return service;
	}

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage message) {
		log.trace("message");
		try {
			java.util.Iterator<com.googlecode.jamr.spi.Outlet> outlets = loader
					.iterator();
			while (outlets.hasNext()) {
				com.googlecode.jamr.spi.Outlet o = outlets.next();
				o.received(message);
			}
		} catch (java.util.ServiceConfigurationError serviceError) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			serviceError.printStackTrace(pw);
			log.error(sw.toString());
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}

	}
}
