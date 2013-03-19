package logview;


import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class LiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LiveServlet.class.getName());
	private int defaultLineCount = 50;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
//		final String uri = req.getRequestURI();
//		final String key = req.getRequestURI().substring(uri.lastIndexOf('/')+1, uri.lastIndexOf('.')).replace("%20", " ");
		final String key = req.getParameter("logid");
		
		int lines = defaultLineCount;
		final String linesParam = req.getParameter("lines");
		if (!StringUtils.isEmpty(linesParam)) {
			try {
				lines = Integer.valueOf(linesParam).intValue();
			} catch (Exception unused) { }
		}
		log.fine("id: "+key);
		final MBeanServer server = InitServlet.getMBeanServer();
		try {
			ObjectName on = ObjectName.getInstance(InitServlet.DOMAIN+":id="+key+",type="+LogFile.class.getName());
			Set mbeans = server.queryMBeans(on, null);
			if (mbeans!=null && mbeans.size()==1) {
				ObjectInstance oi = (ObjectInstance) mbeans.iterator().next();
				String file = (String) server.getAttribute(oi.getObjectName(), "Filename");
				sendLog(resp.getWriter(), file, lines); 
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
	
	private void sendLog(final PrintWriter w, final String file, final int lines) throws IOException {
		Validate.isTrue(lines>0, "lines must be positive greater than zero");
		FileReader reader = new FileReader(file);
		final LineNumberReader br = new LineNumberReader(reader);
		final List<String> queue = new ArrayList<String>();
		String l = null;
		try {
			while ((l = br.readLine())!=null) {
				if (queue.size() > lines) {
					queue.remove(0);
				}
				queue.add(br.getLineNumber()+": "+l);
			}
			w.write("<pre><tt>");
			for (String string : queue) {
				w.write(StringEscapeUtils.escapeHtml(string));
				w.write("\n");
			}
			w.write("</tt></pre>");
			w.flush();
		} finally {
			br.close();
		}
	}
	
	private void sendLog(final PrintWriter w, final String file, final int lines, final int idx) throws IOException {
		Validate.isTrue(lines>0, "lines must be positive greater than zero");
		Validate.isTrue(idx> 0, "Index must be greater than zero");
		final FileReader reader = new FileReader(file);
		final LineNumberReader br = new LineNumberReader(reader);
		final List<String> queue = new ArrayList<String>();
		String l = null;
		int start = idx -lines + 1;
		int end = idx;
		try {
			if (start<1) {
				start = 1;
				end = lines;
			}
			while ((l = br.readLine())!=null) {
				int currentLine = br.getLineNumber();
				if (start <= currentLine && currentLine <=end) {
					queue.add(currentLine + ": " + l);
				}
			}
			if (queue.size() < lines) {
				sendLog(w, file , lines);
			} else {
				w.write("<pre><tt>\n");
				for (String string : queue) {
					w.write(StringEscapeUtils.escapeHtml(string));
					w.write("\n");
				}
				w.write("</tt></pre>");
				w.flush();
			}
		} finally {
			br.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.fine(LiveServlet.class.getName()+" initialized");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		log.fine(LiveServlet.class.getName()+" destroyed");
	}
}
