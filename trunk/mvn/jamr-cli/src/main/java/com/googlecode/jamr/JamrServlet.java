package com.googlecode.jamr;

public class JamrServlet extends javax.servlet.http.HttpServlet {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(JamrServlet.class);

	public void init() throws javax.servlet.ServletException {
		log.trace("Executing INIT");
	}

	public void service(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response)
			throws java.io.IOException {

		String operation = request.getPathInfo();
		log.info("servicing: " + operation);

		if (operation.equals("/dump")) {
			com.googlecode.jamr.PlugUtils pu = new com.googlecode.jamr.PlugUtils();
			org.springframework.context.ApplicationContext context = new org.springframework.context.support.FileSystemXmlApplicationContext(
					"file:" + pu.getConfigPath() + "jdbc");
			com.googlecode.jamr.plug.TargetList targetList = (com.googlecode.jamr.plug.TargetList) context
					.getBean("TargetList");
			java.util.List targets = targetList.getTargets();
			for (int i = 0; i < targets.size(); i++) {
				try {
					com.googlecode.jamr.plug.ErtmDAO dao = (com.googlecode.jamr.plug.ErtmDAO) targets
							.get(i);
					Object obj = dao.getDataSource();
					String details = "";
					if (obj instanceof org.apache.commons.dbcp.BasicDataSource) {
						org.apache.commons.dbcp.BasicDataSource bds = (org.apache.commons.dbcp.BasicDataSource) obj;
						details = bds.getDriverClassName();
					}
					log.info("JDBC: " + details);
				} catch (Exception e) {
					java.io.StringWriter sw = new java.io.StringWriter();
					java.io.PrintWriter pw = new java.io.PrintWriter(sw);
					e.printStackTrace(pw);
					log.error(sw.toString());
				}
			}
		} else {
			response.setContentType("text/html");
			java.io.PrintWriter out = response.getWriter();
			out.println("<pre>");
			out.println("<h1>JAMR</h1>");
			out.println("</pre>");
		}
	}

	public void destroy() {
		log.trace("Executing DESTROY");
	}
}
