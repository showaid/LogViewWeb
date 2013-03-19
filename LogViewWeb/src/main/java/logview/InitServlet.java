package logview;


import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import java.util.logging.SocketHandler;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.SystemUtils;

import logview.mbeans.LogFileManager;
import logview.mbeans.PropertyLogFile;
import logview.mbeans.SocketListener;

public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(InitServlet.class.getName());
	public static final String DEFAULT_CONFIGURATION_FILE = SystemUtils.USER_HOME+SystemUtils.FILE_SEPARATOR+".logview"+SystemUtils.FILE_SEPARATOR+"loggiles.properties";
	public static final String DOMAIN = "logview_"+System.currentTimeMillis();
	private MBeanServer server;
	public static String agentId;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		checkConfDir();
		
		createMBeanServer();
		
		registerCoreMBeans(config);
		
		configureLogging();
		
		log.fine(InitServlet.class.getName()+" initialized");
	}

	private void checkConfDir() {
		final File f = new File(DEFAULT_CONFIGURATION_FILE);
		if (!f.exists()) {
			File dir = f.getParentFile();
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					log.warning("Error creating configuration directory "+dir);
				}
			}
		}
	}

	private synchronized void createMBeanServer() {
		if (server == null) {
			server = MBeanServerFactory.createMBeanServer(DOMAIN);
	
			try {
				agentId = (String) server.getAttribute(ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate"), "MBeanServerId");
				log.info("MBeanServer agent id: "+agentId);
			} catch (Exception e) {
				log.severe("Caught exception in class InitServlet, method createMBeanServer: "+ e);
				throw new RuntimeException("Error creating MBeanServer: "+e);
			}
			
			try {
				// TODO make this port number configurable in web.xml or system property 
				int port = 7000;
				LocateRegistry.createRegistry(port);
				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:"+port+"/jmxrmi");
				JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
				cs.start();
			} catch (Exception e) {
				log.severe("Error creating connector: "+e);
			}
		}
	}

	private void registerCoreMBeans(ServletConfig config) {
		// Add core mbeans
		LogFileManager manager = new LogFileManager();
//		System.out.println(LogFileManager.getObjectName());
		ObjectName on = LogFileManager.getObjectName();
		try {
			server.registerMBean(manager, on);
		} catch (Exception e1) {
			log.warning("Error registering "+on+": "+e1);
		}

		SocketListener listener = new SocketListener();
		try {
			server.registerMBean(listener, SocketListener.getObjectName());
			listener.start();
		} catch (Exception e1) {
			log.warning("Error registering "+SocketListener.getObjectName()+": "+e1);
		}

		// Add MBeans for loggers in logfiles.properties
		try {
			PropertyLogFile logfiles = new PropertyLogFile();
			logfiles.setFilename(DEFAULT_CONFIGURATION_FILE);
			logfiles.addAll();
			ObjectName objectName = logfiles.getObjectName();
			try {
				server.registerMBean(logfiles, objectName);
			} catch (Exception e1) {
				log.info("Error registering "+objectName+": "+ e1);
			}
		} catch (IllegalArgumentException unused) {
			// probably file does not yet exist
			log.fine("NOT AN ERROR: "+ unused);
		}
	}

	private void configureLogging() {
		// TODO Auto-generated method stub
		try {
			// FIXME keep port in sync
			SocketHandler handler = new SocketHandler("127.0.0.1", 7777);
			handler.setFormatter(new SimpleFormatter());
//			LogManager.getLogManager().getLogger("logview").addHandler(handler);
			LogManager.getLogManager().getLogger(InitServlet.class.getName()).addHandler(handler);
		} catch (Exception e) {
			log.severe("Caught exception in class InitServlet, method configureLogging: ");
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		log.fine(InitServlet.class.getName()+" destroyed");
		super.destroy();
		MBeanServerFactory.releaseMBeanServer(this.server);
	}
	
	@SuppressWarnings("unchecked")
	public static MBeanServer getMBeanServer() {
		ArrayList srv = MBeanServerFactory.findMBeanServer(InitServlet.agentId);
		log.fine(""+srv);
		if (srv!=null && srv.size()==1) {
			log.fine("OK, MBeanServer found");
			return (MBeanServer) srv.get(0);
		} else if (srv!=null && srv.size()>1) {
			for (Object object : srv) {
				MBeanServer s = (MBeanServer) object;
				if (s.getDefaultDomain().equals(DOMAIN)) {
					log.fine("OK, MBeanServer found");
					return s;
				}
			}
		}
		log.severe("findMBeanServer did returned "+srv==null?""+0:srv.size()+" servers instead of 1");
		return null;
	}
}
