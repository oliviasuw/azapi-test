package agt0.dev.util;

import java.io.IOException;
import java.io.Writer;

public class StringBuilderWriter extends Writer{
	StringBuilder sb = new StringBuilder();

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public void flush() throws IOException {
		
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		sb.append(cbuf, off, len);
	}
	
	public StringBuilder getStringBuilder(){
		return sb;
	}
}
