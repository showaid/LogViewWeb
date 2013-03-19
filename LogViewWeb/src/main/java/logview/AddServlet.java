package logview;


import java.io.IOException;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logview.mbeans.LogFileManager;

import org.apache.commons.lang.StringUtils;

public class AddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(AddServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String file = req.getParameter("file");
		String id = req.getParameter("id");
		log.fine("file: "+file);
		log.fine("id: "+id);
		if (StringUtils.isEmpty(file)) {
			req.setAttribute("message", "File not added (empty name)");
		} else {
			MBeanServer server = InitServlet.getMBeanServer();
			Set mbeans = server.queryMBeans(LogFileManager.getObjectName(), null);
			if (mbeans!=null && mbeans.size()==1) {
				ObjectInstance oi = (ObjectInstance) mbeans.iterator().next();
				try {
					if (StringUtils.isEmpty(id)) {
						log.fine("invoking addLogFile(\""+file+"\")");
						server.invoke(oi.getObjectName(), "addLogFile", new Object[]{file}, new String[]{String.class.getName()});
					} else {
						// explicit id
						log.fine("invoking addLogFile(\""+id+"\", \""+file+"\")");
						server.invoke(oi.getObjectName(), "addLogFile", new Object[]{id, file}, new String[]{String.class.getName(), String.class.getName()});
					}
				} catch (Exception e) {
					log.severe("Caught exception in class AddServlet, method doPost: "+ e);
					req.setAttribute("message", "Error: "+e);
				}
			} else {
				log.warning("MBean query did not return exactly one result, returned: "+mbeans==null?""+0:""+mbeans.size());
			}
		}
		req.getRequestDispatcher("/add.jsp").forward(req, resp);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(AddServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		log.fine(AddServlet.class.getName()+" destroyed");
		super.destroy();
	}
}
