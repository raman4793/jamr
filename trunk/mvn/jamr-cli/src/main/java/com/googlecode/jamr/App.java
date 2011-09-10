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

public class App {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(App.class);

	public static void main(String[] args) throws Exception {

		gnu.getopt.LongOpt[] longopts = new gnu.getopt.LongOpt[4];
		longopts[0] = new gnu.getopt.LongOpt("help",
				gnu.getopt.LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new gnu.getopt.LongOpt("logging",
				gnu.getopt.LongOpt.OPTIONAL_ARGUMENT, null, 'l');
		longopts[2] = new gnu.getopt.LongOpt("outfile",
				gnu.getopt.LongOpt.OPTIONAL_ARGUMENT, null, 'o');
		longopts[3] = new gnu.getopt.LongOpt("serial",
				gnu.getopt.LongOpt.REQUIRED_ARGUMENT, null, 's');

		gnu.getopt.Getopt g = new gnu.getopt.Getopt("jamr", args, "hl:o:s:",
				longopts);

		String logging = "ERROR";
		String outfile = null;
		String serial = null;

		int c;
		String arg;
		while ((c = g.getopt()) != -1)
			switch (c) {
				case 'l' :
					logging = g.getOptarg();
					break;

				case 'o' :
					outfile = g.getOptarg();
					break;

				case 's' :
					serial = g.getOptarg();
					break;

				default :
					usage();
					break;
			}

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		if (outfile != null) {

			ch.qos.logback.classic.LoggerContext lc = root.getLoggerContext();

			ch.qos.logback.core.FileAppender fa = new ch.qos.logback.core.FileAppender();
			fa.setFile(outfile);
			ch.qos.logback.classic.PatternLayout pl = new ch.qos.logback.classic.PatternLayout();
			pl.setPattern("%d %5p %t [%c:%L] %m%n)");
			pl.setContext(lc);
			pl.start();
			fa.setLayout(pl);
			fa.setContext(lc);
			fa.setName("FILE");
			fa.start();
			root.addAppender(fa);
		}

		log.info("CLI " + logging + " " + serial);

		if (logging.toUpperCase().equals("TRACE")) {
			root.setLevel(ch.qos.logback.classic.Level.TRACE);
		} else if (logging.toUpperCase().equals("DEBUG")) {
			root.setLevel(ch.qos.logback.classic.Level.DEBUG);
		} else if (logging.toUpperCase().equals("INFO")) {
			root.setLevel(ch.qos.logback.classic.Level.INFO);
		} else if (logging.toUpperCase().equals("WARN")) {
			root.setLevel(ch.qos.logback.classic.Level.WARN);
		} else {
			root.setLevel(ch.qos.logback.classic.Level.ERROR);
		}

		if (serial == null) {
			usage();
		}

		SerialUtils su = SerialUtils.getInstance();
		try {
			su.connect(serial);
		} catch (java.lang.Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}

	public static void usage() {
		System.out.println("usage: jamr [ -hls ]\n"
				+ "   [ -l ] [ --logging= ] ( INFO, DEBUG )\n"
				+ "   [ -o ] [ --outfile= ] ( /tmp/jamr.log )\n"
				+ "   [ -s ] [ --serial= ] ( /dev/ttyACM0 )\n"
				+ "   [ -h ] [ --help ]\n");

		System.exit(1);
	}
}
