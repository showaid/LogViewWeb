package logview.mbeans;


public interface PropertyLogFileMBean {
	void setFilename(String filepath);
	void addAll();
	void removeAll();
}
