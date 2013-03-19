package logview;


import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logview.mbeans.LogFile;

public class ListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(ListServlet.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Properties p = new Properties();
		
		try {
			final MBeanServer server = InitServlet.getMBeanServer();
			final Set mbeans = server.queryMBeans(ObjectName.getInstance(InitServlet.DOMAIN+":*,type="+LogFile.class.getName()), null);
			log.fine("MBeans found: "+(mbeans==null?0:mbeans.size()));
			for (Object on : mbeans) {
				ObjectInstance oi = ((ObjectInstance)on);
				if (log.isLoggable(Level.FINE)) {
					log.fine("classname: "+oi.getClassName()+"; objectname: "+oi.getObjectName());
					MBeanInfo info = server.getMBeanInfo(oi.getObjectName());
//					log.fine(""+info);
					for (MBeanOperationInfo o : info.getOperations()) {
						log.fine(""+o);
						log.fine("name: "+o.getName());
					}
				}

				String key = (String) server.getAttribute(oi.getObjectName(), "Id");
				String value = (String) server.getAttribute(oi.getObjectName(), "Filename");

				p.put(key, value);
			}
		} catch (Exception e) {
			log.severe("Caught exception in class ListServlet, method doGet: "+ e);
			throw new RuntimeException("Error listing logs: "+e);
		}
		req.setAttribute("Map", p);
		req.getRequestDispatcher("/list.jsp").forward(req, resp);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(ListServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		log.fine(ListServlet.class.getName()+" destroyed");
	}
}
