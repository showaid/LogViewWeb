package logview.mbeans;

import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import logview.InitServlet;

import org.apache.commons.lang.StringUtils;

public class LogFileManager implements LogFileManagerMBean {
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogFileManager.class.getName());
	
	@SuppressWarnings("unchecked")
	public LogFileManager() {
		
	}
	
	public void addLogFile(String id, String logfile) {
		log.finer("addLogFile: "+id+", "+logfile);
		LogFile mbean = new LogFile();
		mbean.setFilename(logfile);
		mbean.setId(id);
		MBeanServer server = InitServlet.getMBeanServer();
		try {
			ObjectInstance oi = server.registerMBean(mbean, ObjectName.getInstance(InitServlet.DOMAIN+":id="+id+",type="+LogFile.class.getName()));
			log.info("Registered "+oi.getObjectName());
		} catch (Exception e) {
			log.severe("Caught exception in class LogFileManager, method addLogFile: "+ e);
			throw new RuntimeException("Registration of "+mbean+" failed: "+e);
		}
	}

	public static ObjectName getObjectName() {
		try {
			return ObjectName.getInstance(InitServlet.DOMAIN+":id=LogFileManager,type="+LogFileManager.class.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addLogFile(String logfile) {
		log.finer("addLogFile: "+logfile);
		String id = StringUtils.replaceChars(logfile, '/', '_');
		id = StringUtils.replaceChars(logfile, '\\', '_');
		id = StringUtils.replaceChars(id, '.', '-');
		id = StringUtils.replaceChars(id, ':', '_');
		this.addLogFile(id, logfile);
	}

	@SuppressWarnings("unchecked")
	public Integer getLogCount() {
		int count = 0;
		MBeanServer server = InitServlet.getMBeanServer();
		Set mbeans = server.queryMBeans(null, null);
		for (Object on : mbeans) {
			ObjectInstance oi = ((ObjectInstance)on);
			// TODO use proper query
			if (oi.getClassName().endsWith(LogFile.class.getName())) {
				log.fine("classname: "+oi.getClassName()+"; objectname: "+oi.getObjectName());
				count++;
			}
		}
		log.fine("count: "+count);
		return new Integer(count);
	}
	
}
