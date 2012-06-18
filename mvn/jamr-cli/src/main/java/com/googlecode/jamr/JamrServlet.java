package com.googlecode.jamr;

public class JamrServlet extends javax.servlet.http.HttpServlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(JamrServlet.class);

	private org.springframework.context.ApplicationContext context;
	private org.apache.commons.dbcp.BasicDataSource ds;

	public void init() throws javax.servlet.ServletException {
		log.trace("Executing INIT");

		com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();

		java.io.File config = new java.io.File(pu.getConfigPath() + "jdbc");
		if (config.exists()) {
			context = new org.springframework.context.support.FileSystemXmlApplicationContext(
					"file:" + pu.getConfigPath() + "jdbc");
			ds = (org.apache.commons.dbcp.BasicDataSource) context
					.getBean("h2DataSource");
		}
	}

	public void service(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response)
			throws java.io.IOException {

		String operation = request.getPathInfo();
		log.info("servicing: " + operation);

		String andWhere = "";
		String meter = request.getParameter("meter");
		if (meter != null) {
			andWhere = " and serial = '" + meter + "'";
		}

		String after = request.getParameter("after");
		if (after != null) {
			long aft = new Long(after).longValue();
			java.sql.Timestamp ts = new java.sql.Timestamp(aft);
			andWhere = andWhere + " and recorded_at > '" + ts + "'";
		}

		if (context == null) {
			log.info("No context");
			return;
		}

		try {
			java.util.List output = new java.util.Vector();

			java.sql.Connection conn = ds.getConnection();
			if (operation.equals("/favicon.ico")) {
			} else if (operation.equals("/dump")) {
				String sql = "select serial, recorded_at, reading from meters where 1 = 1 "
						+ andWhere;
				log.info("SQL: " + sql);
				java.sql.PreparedStatement ps = conn.prepareStatement(sql);
				//ps.setString( 1, guid );
				java.sql.ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					java.util.Hashtable record = new java.util.Hashtable();
					record.put("meter", rs.getString(1));
					record.put("timestamp", rs.getTimestamp(2).getTime());
					record.put("reading", rs.getString(3));
					output.add(record);
				}
			} else if (operation.equals("/dumpTargets")) {
				com.googlecode.jamr.plug.TargetList targetList = (com.googlecode.jamr.plug.TargetList) context
						.getBean("TargetList");
				java.util.List targets = targetList.getTargets();
				for (int i = 0; i < targets.size(); i++) {
					com.googlecode.jamr.plug.ErtmDAO dao = (com.googlecode.jamr.plug.ErtmDAO) targets
							.get(i);
					Object obj = dao.getDataSource();
					if (obj instanceof org.apache.commons.dbcp.BasicDataSource) {
						org.apache.commons.dbcp.BasicDataSource bds = (org.apache.commons.dbcp.BasicDataSource) obj;
						output.add("JDBC: " + bds.getDriverClassName());
					}
				}
			} else if (operation.equals("/meters")) {
				String sql = "select distinct serial from meters order by serial";
				log.info("SQL: " + sql);
				java.sql.PreparedStatement ps = conn.prepareStatement(sql);
				java.sql.ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					output.add(rs.getString(1));
				}
			}

			if (output.size() > 0) {
				log.debug("Size of result set: " + output.size());
				com.google.gson.GsonBuilder gsonBuild = new com.google.gson.GsonBuilder();
				gsonBuild.setPrettyPrinting();
				com.google.gson.Gson gson = gsonBuild.create();
				response.setContentType("application/json");
				String json = gson.toJson(output);
				javax.servlet.ServletOutputStream sOutStream = response
						.getOutputStream();
				sOutStream.print(json);
			} else {
				response.setContentType("text/html");
				java.io.PrintWriter out = response.getWriter();
				out.println("<pre>");
				out.println("<h1>JAMR</h1>");
				out.println("</pre>");
			}

		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}
	public void destroy() {
		log.trace("Executing DESTROY");
	}
}
