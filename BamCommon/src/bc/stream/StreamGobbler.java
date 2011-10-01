/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class StreamGobbler extends Thread{
    OutputStream redirect = null;
    InputStream in;

    public StreamGobbler(InputStream in) {
        this.in = in;
    }

    public void setRedirect(OutputStream redirect) {
        this.redirect = redirect;
    }

    public OutputStream getRedirect() {
        return redirect;
    }

    @Override
    public void run() {
        try {
            int b = 1984;
            while (!isInterrupted() && (b = in.read()) >= 0){
                if (redirect != null){
                    redirect.write(b);
                }
            }

            System.out.println("Exit With b=" + b);
        } catch (IOException ex) {
            Logger.getLogger(StreamGobbler.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if (redirect != null){
                try {
                    redirect.write("EXECUTION DONE.\n".getBytes());
                    redirect.flush();
                } catch (IOException ex) {
                    Logger.getLogger(StreamGobbler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }
    
}
