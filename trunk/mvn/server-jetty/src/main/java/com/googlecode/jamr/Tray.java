package com.googlecode.jamr;

public class Tray extends UI implements java.util.Observer {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(Tray.class);
	private java.awt.TrayIcon trayIcon;

	Tray(JamrModel jamr) {
		super(jamr);

		trayIcon = new java.awt.TrayIcon(image, "Jamr", null);

		final javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
		popup.addSeparator();
		popup.add(defaultItem);
		popup.addSeparator();

		trayIcon.setImageAutoSize(true);

		trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.setLocation(e.getX(), e.getY());
					popup.setInvoker(popup);
					popup.setVisible(true);
				}
			}
		});

		try {
			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			tray.add(trayIcon);
		} catch (java.awt.AWTException e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}

	public void update(java.util.Observable t, Object o) {
	}
}
