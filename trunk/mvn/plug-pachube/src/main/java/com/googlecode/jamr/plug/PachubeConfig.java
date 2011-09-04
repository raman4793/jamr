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

public class PachubeConfig {
	private java.util.Hashtable keys;
	private java.util.Hashtable feeds;
	private java.util.Hashtable streams;

	public java.util.Hashtable getKeys() {
		return keys;
	}

	public void setKeys(java.util.Hashtable k) {
		this.keys = k;
	}

	public java.util.Hashtable getFeeds() {
		return feeds;
	}

	public void setFeeds(java.util.Hashtable f) {
		feeds = f;
	}

	public java.util.Hashtable getStreams() {
		return streams;
	}

	public void setStreams(java.util.Hashtable s) {
		streams = s;
	}
}
