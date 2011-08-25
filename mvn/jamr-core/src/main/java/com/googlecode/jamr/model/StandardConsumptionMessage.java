/**
 * License
 */

package com.googlecode.jamr.model;

public class StandardConsumptionMessage
		extends
			EncoderReceiverTransmitterMessage {
	public StandardConsumptionMessage(String parts[]) {
		setSerial(parts[1]);
		setReading(new java.math.BigDecimal(parts[3]));

		if (parts.length > 4) {
			setFrequency(Short.parseShort(parts[4]));
		}

		if (parts.length > 5) {
			setSignalStrength(Short.parseShort(parts[5]));
		}
	}
}
