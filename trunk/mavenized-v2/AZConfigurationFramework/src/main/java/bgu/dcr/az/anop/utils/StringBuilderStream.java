/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author User
 */
public class StringBuilderStream extends OutputStream {

    StringBuilder sb = new StringBuilder();

    @Override
    public void write(int b) throws IOException {
        sb.append((char) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        sb.append(new String(b));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        sb.append(new String(b, off, len));
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

}
