package logview.mbeans;

import java.io.File;

import javax.management.ObjectName;

import logview.InitServlet;

public class LogFile implements LogFileMBean {
	private String id;
	private String filename;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogFile.class.getName());
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		File f = new File(filename);
		if (!f.exists()) {
			throw new IllegalArgumentException("File '"+filename+"' does not exist");
		}
		if (!f.canRead()) {
			throw new IllegalArgumentException(filename+" is unreadable");
		}
		this.filename = filename;
	}
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);
	}
	public ObjectName getObjectName() {
		if (id==null)
			throw new IllegalStateException("id is null");
		try {
			return ObjectName.getInstance(InitServlet.DOMAIN+":id="+id+",type="+LogFile.class.getName());
		} catch (Exception e) {
			log.severe("Caught exception in class LogFile, method getObjectName: "+ e);
		}
		return null;
	}
}
