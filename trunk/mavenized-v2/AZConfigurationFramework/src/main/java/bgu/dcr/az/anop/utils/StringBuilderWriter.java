/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author User
 */
public class StringBuilderWriter extends Writer {

    StringBuilder sb = new StringBuilder();

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

    @Override
    public String toString() {
        return sb.toString();
    }

}
