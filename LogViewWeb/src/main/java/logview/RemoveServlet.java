package logview;


import java.io.IOException;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import logview.mbeans.LogFile;

public class RemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(RemoveServlet.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final MBeanServer server = InitServlet.getMBeanServer();
		final String id = req.getParameter("id");
		try {
			final Set mbeans;
			if (!StringUtils.isEmpty(id)) {
				log.fine("About to remove logfile with id "+id);
				mbeans = server.queryMBeans(ObjectName.getInstance(InitServlet.DOMAIN+":id="+id+",type="+LogFile.class.getName()), null);
			} else {
				// If no id is given, remove all (old behavior)
				mbeans = server.queryMBeans(ObjectName.getInstance(InitServlet.DOMAIN+":*,type="+LogFile.class.getName()), null);
			}
			for (Object on : mbeans) {
				ObjectInstance oi = ((ObjectInstance)on);
				log.fine("Removing "+oi.getObjectName());
				server.unregisterMBean(oi.getObjectName());
			}
			req.getRequestDispatcher("list").forward(req, resp);
//			resp.getWriter().print("OK");
		} catch (Exception e) {
			log.severe("Caught exception in class RemoveAllServlet, method doGet: "+ e);
			resp.getWriter().print("ERR: "+e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(RemoveServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		log.fine(RemoveServlet.class.getName()+" destroyed");
	}
}
