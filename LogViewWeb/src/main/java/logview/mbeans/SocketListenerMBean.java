package logview.mbeans;

import java.io.IOException;

public interface SocketListenerMBean {

	int getPort() ;

	void setPort(int port) ;

	void start() throws IOException ;

	void stop() throws IOException, InterruptedException ;

}
