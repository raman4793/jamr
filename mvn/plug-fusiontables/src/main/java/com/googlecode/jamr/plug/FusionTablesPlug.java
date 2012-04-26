package com.googlecode.jamr.plug;

public class FusionTablesPlug implements com.googlecode.jamr.spi.Outlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(FusionTablesPlug.class);

	private FusionTablesConfig ftc;

	public FusionTablesPlug() {
		log.trace("init");

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();
		com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream();

		java.io.File file = pu.getConfigFile("fusion");
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(file);
			ftc = (FusionTablesConfig) xstream.fromXML(fis);
		} catch (java.io.FileNotFoundException fnfe) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			fnfe.printStackTrace(pw);
			log.error(sw.toString());

			// TODO install empty config ??
			ftc = new FusionTablesConfig();
		}

		try {

			java.net.URL url = new java.net.URL(
					"https://www.google.com/fusiontables/api/query?encid=true");

			com.google.gdata.client.GoogleService service = new com.google.gdata.client.GoogleService(
					"fusiontables", "fusiontables.ApiExample");
			service.setUserCredentials(ftc.getEmail(), ftc.getPassword(),
					com.google.gdata.client.ClientLoginAccountType.GOOGLE);

			com.google.gdata.client.Service.GDataRequest request = service
					.getRequestFactory()
					.getRequest(
							com.google.gdata.client.Service.GDataRequest.RequestType.INSERT,
							url,
							new com.google.gdata.util.ContentType(
									"application/x-www-form-urlencoded"));
			java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
					request.getRequestStream());
			writer.append("sql="
					+ java.net.URLEncoder.encode(
							"CREATE TABLE demo (name:STRING, date:DATETIME)",
							"UTF-8"));
			writer.flush();

			request.execute();

		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}

	}

	public void received(
			com.googlecode.jamr.model.EncoderReceiverTransmitterMessage ert) {
		String serial = ert.getSerial();
		log.trace("received serial: " + serial);
	}
}
