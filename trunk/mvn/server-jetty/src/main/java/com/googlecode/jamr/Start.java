package com.googlecode.jamr;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import java.security.ProtectionDomain;
import java.net.URL;

public class Start {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(Start.class);
	public static void main(String[] args) {

		java.util.Properties properties = System.getProperties();
		String home = properties.getProperty("user.home");
		String outfile = home + "/.jamr/jamr.log";

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		root.detachAndStopAllAppenders();

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
		root.setLevel(ch.qos.logback.classic.Level.TRACE);

		JamrModel jamr = new JamrModel();

		try {
			if (java.awt.SystemTray.isSupported()) {
				Tray t = new Tray(jamr);
			}
		} catch (java.lang.NoClassDefFoundError e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}

		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(9090);
		server.setConnectors(new Connector[]{connector});

		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/");

		ProtectionDomain protectionDomain = Start.class.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		context.setWar(location.toExternalForm());

		server.setHandler(context);
		try {
			server.start();
			System.in.read();
			server.stop();
			server.join();
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
			System.exit(100);
		}
	}
}
