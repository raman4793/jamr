/**
 * License
 */

package com.googlecode.jamr.spi;

public interface Outlet {
	void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage message);
}
