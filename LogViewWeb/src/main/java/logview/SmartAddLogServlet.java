package logview;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logview.mbeans.LogFile;

import org.apache.commons.lang.SystemUtils;

public class SmartAddLogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(SmartAddLogServlet.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<File> files = new ArrayList<File>();
		try {
			final String tomcatHome = System.getProperties().getProperty("catalina.home");
			if (tomcatHome != null) {
				File logsDir = new File(tomcatHome+File.separatorChar+"logs");
				if (logsDir.exists() && logsDir.isDirectory()) {
					files.addAll(Arrays.asList(logsDir.listFiles()));
				}
			}
		} catch (Exception unused) {
		}

		try {
			final String resinHome = System.getProperties().getProperty("resin.home");
			if (resinHome != null) {
				File logsDir = new File(resinHome+File.separatorChar+"logs");
				if (logsDir.exists() && logsDir.isDirectory()) {
					files.addAll(Arrays.asList(logsDir.listFiles()));
				}
			}
		} catch (Exception unused) {
		}

		try {
			final String tmp = SystemUtils.JAVA_IO_TMPDIR;
			if (tmp != null) {
				File tmpDir = new File(tmp+File.separatorChar);
				if (tmpDir.exists() && tmpDir.isDirectory()) {
					files.addAll(Arrays.asList(tmpDir.listFiles()));
				}
			}
		} catch (Exception unused) {
		}
		
		try {
			final String tmp = "D:\\tmp";
			File tmpDir = new File(tmp);
			if (tmpDir.exists() && tmpDir.isDirectory()) {
				files.addAll(Arrays.asList(tmpDir.listFiles()));
			}
		} catch (Exception unused) {
		}
		Collections.sort(files);

		if (log.isLoggable(Level.FINER)) {
			log.finer("files: "+files);
		}
		/*
		 * Remove from list logfiles alredy registered
		 */
		final MBeanServer server = InitServlet.getMBeanServer();
		try {
			final Set mbeans = server.queryMBeans(ObjectName.getInstance(InitServlet.DOMAIN+":*,type="+LogFile.class.getName()), null);
			log.fine("MBeans found: "+(mbeans==null?0:mbeans.size()));
			for (Object on : mbeans) {
				ObjectInstance oi = ((ObjectInstance)on);
				try {
					String value = (String) server.getAttribute(oi.getObjectName(), "Filename");
					log.finer("Checking presence of "+value);
					if (value!=null) {
						for (int i = 0 ; i<files.size() ; i++) {
							File f = files.get(i);
							if (f.toString().equals(value)) {
								files.remove(i);
								break;
							}
						}
					}
				} catch (Exception e) {
					log.severe("Caught exception in class SmartAddLogServlet, method doGet: " + e);
				}
			}
		} catch (Exception e) {
			log.severe("Caught exception in class SmartAddLogServlet, method doGet: " + e);
		}

		
		req.setAttribute("List", files);
		req.getRequestDispatcher("/smartadd.jsp").forward(req, resp);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(SmartAddLogServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		log.fine(SmartAddLogServlet.class.getName()+" destroyed");
		super.destroy();
	}
}
