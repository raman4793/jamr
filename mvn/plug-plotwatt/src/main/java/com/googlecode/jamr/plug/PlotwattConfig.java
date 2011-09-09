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

public class PlotwattConfig {
	private String houseCode;
	private java.util.Hashtable meters;

	public String getHouseCode() {
		return houseCode;
	}

	public void setHouseCode(String hc) {
		this.houseCode = hc;
	}

	public java.util.Hashtable getMeters() {
		return meters;
	}

	public void setMeters(java.util.Hashtable m) {
		this.meters = m;
	}
}
