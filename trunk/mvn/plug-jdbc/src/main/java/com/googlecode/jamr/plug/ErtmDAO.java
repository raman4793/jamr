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

public class ErtmDAO {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ErtmDAO.class);

	private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
	private String createStatement;
	private String insertStatement;
	private String verifyStatement;

	public void setDataSource(javax.sql.DataSource dataSource) {
		this.jdbcTemplate = new org.springframework.jdbc.core.JdbcTemplate(
				dataSource);
	}

	public javax.sql.DataSource getDataSource() {
		return (jdbcTemplate.getDataSource());
	}

	public void setCreateStatement(String c) {
		this.createStatement = c;
	}

	public void setInsertStatement(String i) {
		this.insertStatement = i;
	}

	public void setVerifyStatement(String v) {
		this.verifyStatement = v;
	}

	public void createTable() {
		jdbcTemplate.update(createStatement);
	}

	public void insertErt(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		jdbcTemplate.update(insertStatement, new Object[]{ert.getSerial(),
				ert.getDate(), ert.getReading()});
	}

	public int verify() {
		return jdbcTemplate.queryForInt(verifyStatement);
	}
}
