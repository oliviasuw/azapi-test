/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.srccon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class StreamGobbler extends Thread {
    BufferedReader in;

    private StreamGobbler(InputStream in) {
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.out.println("Gobbler done.");
        }
    }
    
    public static StreamGobbler gobble(InputStream in){
        StreamGobbler gob = new StreamGobbler(in);
        gob.start();
        return gob;
    }
}
