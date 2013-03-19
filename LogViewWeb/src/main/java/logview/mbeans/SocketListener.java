package logview.mbeans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.management.ObjectName;

import logview.Buffer;
import logview.InitServlet;

public class SocketListener implements Runnable, SocketListenerMBean {
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(SocketListener.class.getName());

	private ServerSocket serverSocket = null;

	private int port = 7777;

	private Thread serverThread;

	private boolean listening;

	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		listening = true;
		this.serverThread = new Thread(this);
		serverThread.start();
	}

	public void stop() throws IOException, InterruptedException {
		listening = false;
		serverThread.join();
		serverSocket.close();
	}

	public void run() {
		while (listening) {
			try {
				new SocketReader(serverSocket.accept()).start();
			} catch (IOException e) {
				log.severe("Caught exception in class SocketListener, method run: " + e);
			}
		}
	}

	private static final class SocketReader extends Thread {

		private Socket sock;

		public SocketReader(Socket accept) {
			this.sock = accept;
		}

		public void run() {
			try {
				log.fine("Accepted connection");
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				String inputLine;
				Buffer b = Buffer.getInstance();
				while ((inputLine = in.readLine()) != null) {
					System.out.println("| "+inputLine);
					b.add(inputLine);
				}
				in.close();
				
				sock.close();
				log.fine("closing connection");
			} catch (IOException e) {
				log.severe("Caught exception in class SocketReader, method run: " + e);
			}
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public static ObjectName getObjectName() {
		try {
			return ObjectName.getInstance(InitServlet.DOMAIN+":id=SocketListener,type="+SocketListener.class.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
