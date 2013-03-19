package logview.mbeans;

public interface LogFileManagerMBean {

	void addLogFile(String id, String logfile);
	
	void addLogFile(String logfile);
	
	Integer getLogCount();
}
