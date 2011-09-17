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

public class PlugUtils {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(PlugUtils.class);

	public String getConfigPath() {
		java.util.Properties properties = System.getProperties();
		String home = properties.getProperty("user.home");

		return (home + "/.jamr/");
	}

	public java.io.File getConfigFile(String plugname) {

		java.io.File file = new java.io.File(getConfigPath() + plugname);

		return (file);
	}
}
