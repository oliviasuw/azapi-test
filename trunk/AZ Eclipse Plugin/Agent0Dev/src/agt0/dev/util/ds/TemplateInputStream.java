package agt0.dev.util.ds;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TemplateInputStream extends InputStream {

	private Map<String, String> fills;
	private InputStream from;
	private ArrayList<Integer> buf;

	public TemplateInputStream(Map<String, String> fills, InputStream from) {
		this.fills = fills;
		this.from = from;
		buf = new ArrayList<Integer>(30);
	}

	@Override
	public int read() throws IOException {
		if (buf.isEmpty()) {
			int ret = from.read();
			if (ret == (int) '$') {
				ret = from.read();
				if (ret == (int) '{') {
					String vname = readVar();
					for (byte b : ("" + fills.get(vname)).getBytes()) {
						buf.add((int) b);
					}
				}else{
					buf.add((int)'$');
					buf.add(ret);
				}
			}else {
				buf.add(ret);
			}
		}
		
		return buf.remove(0);
	}

	private String readVar() throws IOException {
		StringBuilder sb = new StringBuilder();
		int r;
		while ((r = from.read()) > 0 && r != (int)'}'){
			sb.append((char) r);
		}
		
		return sb.toString();
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		from.close();
	}

}
