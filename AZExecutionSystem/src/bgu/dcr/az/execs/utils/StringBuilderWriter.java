/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.utils;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author bennyl
 */
public class StringBuilderWriter extends Writer {

    StringBuilder sb;

    public StringBuilderWriter(StringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        sb.append(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

    
    
}
