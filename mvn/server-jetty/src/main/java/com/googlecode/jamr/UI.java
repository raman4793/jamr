package com.googlecode.jamr;

abstract class UI implements java.util.Observer {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(UI.class);

	protected JamrModel jamr;
	protected java.awt.Image image;
	protected javax.swing.JMenuItem defaultItem;
	UI(JamrModel jamr) {
		this.jamr = jamr;

		java.lang.ClassLoader cl = Thread.currentThread()
				.getContextClassLoader();
		image = java.awt.Toolkit.getDefaultToolkit().getImage(
				cl.getResource("images/tray.gif"));
		defaultItem = new javax.swing.JMenuItem("Exit");
		defaultItem.addActionListener(new UIActionListener());

		jamr.addObserver(this); // Connect the View to the Model
	}

	class UIActionListener implements java.awt.event.ActionListener {

		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getActionCommand().equals("Exit")) {

				try {
					jamr.getServer().stop();
					jamr.getServer().join();
				} catch (Exception exc) {
					java.io.StringWriter sw = new java.io.StringWriter();
					java.io.PrintWriter pw = new java.io.PrintWriter(sw);
					exc.printStackTrace(pw);
					log.error(sw.toString());
				}

				System.exit(0);
			}
		}
	}
}
