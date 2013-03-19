package logview;


import java.util.ArrayList;
import java.util.List;

public class Buffer {
	private List<String>lines = new ArrayList<String>(100);
	private static final Buffer instance = new Buffer();
	
	private Buffer(){}
	
	public static final Buffer getInstance() {
		return instance;
	}
	
	public void add(String l) {
		this.lines.add(l);
		if (this.lines.size()>100) {
			this.lines.remove(0);
		}
	}
	
	public String get() {
		StringBuilder sb = new StringBuilder();
		for (String s : lines) {
			sb.append(s).append('\n');
		}
		return sb.toString();
	}
}
