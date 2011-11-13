package agt0.dev.util.conc;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author bennyl
 */
public class StreamGobbler extends Thread {

    private InputStream in;
    private OutputStream out;

    public StreamGobbler(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[2048];
            
            while (true) {
                final int r = in.read(buf);
                if (r == -1) return;
                out.write(buf, 0, r);
            }

        } catch (Exception ex) {
            //do nothing .. 
        }finally{
            System.out.println("Stream Gobbler exit");
        }
    }
}
