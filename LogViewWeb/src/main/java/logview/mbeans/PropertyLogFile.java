package logview.mbeans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import logview.InitServlet;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

public class PropertyLogFile implements PropertyLogFileMBean {
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PropertyLogFile.class.getName());
	private File file;
	
	@SuppressWarnings("unchecked")
	public PropertyLogFile() {
	}
	
	public void addAll() {
		final Properties p = new Properties();
		try {
			p.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			log.severe("Caught exception in class PropertyLogFile, method addAll: "+ e);
			return;
		}
		MBeanServer server = InitServlet.getMBeanServer();
		for (Object id : p.keySet()) {
			Object file = p.get(id);
			
			// Create logfile MBean and register it
			LogFile l = new LogFile();
			l.setId((String)id);
			l.setFilename((String)file);
			try {
				ObjectName objectName = l.getObjectName();
				log.fine("Registering "+objectName);
//				ObjectInstance oi = server.createMBean(LogFile.class.getName(), objectName);
//				server.invoke(oi.getObjectName(), "setId", new Object[]{id}, new String[]{String.class.getName()});
				server.registerMBean(l, objectName);
			} catch (Exception e) {
				log.severe("Caught exception in class PropertyLogFile, method addAll: "+ e);
			}
		}

	}

	public void removeAll() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void setFilename(String filepath) {
		File f = new File(filepath);
		if (!f.exists()) {
			throw new IllegalArgumentException("file "+filepath+" does not exist");
		}
		if (!f.canRead()) {
			throw new IllegalArgumentException("file "+filepath+" is unreadable");
		}
		this.file = f;
	}
	
	public ObjectName getObjectName() {
		if (this.file==null)
			throw new IllegalStateException("filepath is null");
		try {
//			return ObjectName.getInstance(InitServlet.DOMAIN+":filepath="+this.file.getAbsolutePath()+",type="+PropertyLogFile.class.getName());
			String filePath = this.file.getAbsolutePath();
			filePath = StringUtils.replaceChars(filePath, ':', '_');
			String propertyName = InitServlet.DOMAIN+":filepath="+filePath+",type="+PropertyLogFile.class.getName();
//			System.out.println(propertyName);
			return ObjectName.getInstance(propertyName);
		} catch (Exception e) {
			log.severe("Caught exception in class PropertyLogFile, method getObjectName: "+ e);
		}
		return null;
	}

}
