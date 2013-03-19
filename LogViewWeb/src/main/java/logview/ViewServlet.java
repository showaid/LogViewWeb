package logview;


import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logview.mbeans.LogFile;

import org.apache.commons.lang.StringEscapeUtils;

public class ViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(ViewServlet.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		final String uri = req.getRequestURI();
//		final String key = req.getRequestURI().substring(uri.lastIndexOf('/')+1, uri.lastIndexOf('.')).replace("%20", " ");
		final String key = req.getParameter("logid");
		log.fine("id: "+key);
		final MBeanServer server = InitServlet.getMBeanServer();
		
		try {
			ObjectName on = ObjectName.getInstance(InitServlet.DOMAIN+":id="+key+",type="+LogFile.class.getName());
			Set mbeans = server.queryMBeans(on, null);
			if (mbeans!=null && mbeans.size()==1) {
				ObjectInstance oi = (ObjectInstance) mbeans.iterator().next();
				String file = (String) server.getAttribute(oi.getObjectName(), "Filename");
//				req.getRequestDispatcher("/view.jsp").include(req, resp);
				sendLog(resp.getWriter(), new FileReader(file)); 
			} else {
				throw new NoSuchElementException("MBean "+on+" not found");
			}
		} catch (Exception e) {
			throw new ServletException(e.toString());
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	private void sendLog(final PrintWriter w, final Reader reader) throws IOException {
		try {
			w.write("<pre><tt>");
			final LineNumberReader br = new LineNumberReader(reader);
			String l = null;
			while ((l = br.readLine())!=null) {
				w.write(br.getLineNumber()+": ");
				w.write(StringEscapeUtils.escapeHtml(l));
				w.write("\n");
			}

			w.write("</tt></pre>");
			w.flush();
		} finally {
			reader.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(ViewServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		log.fine(ViewServlet.class.getName()+" destroyed");
	}
}
