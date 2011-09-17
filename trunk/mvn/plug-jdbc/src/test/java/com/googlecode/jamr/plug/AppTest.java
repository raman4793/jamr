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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AppTest.class);

	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite testsToRun = new TestSuite();
		testsToRun.addTest(new AppTest("mysql"));

		return (testsToRun);
	}

	public void mysql() throws Exception {

		JdbcPlug plug = new JdbcPlug();

		com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert = new com.googlecode.jamr.model.EncoderReceiverTransmitterMessage();
		ert.setDate(new java.util.Date());
		ert.setSerial("test01");
		ert.setReading(new java.math.BigDecimal(10101010));

		plug.received(ert);
	}
}
