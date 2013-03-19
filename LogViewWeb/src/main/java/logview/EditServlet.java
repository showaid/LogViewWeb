package logview;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class EditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(EditServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Properties p = new Properties();
		try {
			p.load(new FileInputStream(InitServlet.DEFAULT_CONFIGURATION_FILE));
		} catch (FileNotFoundException unused) {
			// If config file is not found it will be created
		}
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		p.store(o, null);
		// FIXME these values will not be considered until an app restart
		
		req.setAttribute("content", new String(o.toByteArray()));
		req.setAttribute("message", "Application needs to be restarted for taking this values online, this limitation will be fixed in a near future");
		req.getRequestDispatcher("/config.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Save and then do the same as get
		String content = req.getParameter("content");
		if (StringUtils.isEmpty(content)) {
			throw new RuntimeException("Refusing to write empty configuration file");
		} else {
			FileOutputStream fos = new FileOutputStream(InitServlet.DEFAULT_CONFIGURATION_FILE);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			doGet(req, resp);
		}
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(EditServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		log.fine(EditServlet.class.getName()+" destroyed");
		super.destroy();
	}
}
