package logview;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logview.mbeans.LogFile;

public class SaveAllServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(SaveAllServlet.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Save and then do the same as get
		final Properties p = new Properties();
		final MBeanServer server = InitServlet.getMBeanServer();
		try {
			final Set mbeans = server.queryMBeans(ObjectName.getInstance(InitServlet.DOMAIN+":*,type="+LogFile.class.getName()), null);
			for (Object on : mbeans) {
				ObjectInstance oi = ((ObjectInstance)on);
				String key = (String) server.getAttribute(oi.getObjectName(), "Id");
				String value = (String) server.getAttribute(oi.getObjectName(), "Filename");

				p.put(key, value);
			}
			FileOutputStream o = new FileOutputStream(InitServlet.DEFAULT_CONFIGURATION_FILE);
			p.store(o, null);
			o.close();
//			resp.getWriter().print("OK");
			req.getRequestDispatcher("list").forward(req, resp);
		} catch (Exception e) {
			log.severe("Caught exception in class SaveAllServlet, method doGet: "+ e);
			resp.getWriter().print("ERR: "+e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(SaveAllServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		log.fine(SaveAllServlet.class.getName()+" destroyed");
		super.destroy();
	}
}
