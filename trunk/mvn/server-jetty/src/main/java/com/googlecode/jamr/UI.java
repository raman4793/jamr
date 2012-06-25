package com.googlecode.jamr;

abstract class UI implements java.util.Observer {
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
				System.exit(0);
			}
		}
	}
}
