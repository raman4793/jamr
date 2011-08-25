/**
 * License
 */

package com.googlecode.jamr.plug;

public class StdOutPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(StdOutPlug.class);

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		String serial = ert.getSerial();
		log.trace("received serial: " + serial);
	}
}
