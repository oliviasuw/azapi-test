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
import java.util.regex.Pattern;

/**
 *
 * @author BLutati
 */
public class StopReader {

    InputStream is;
    StringBuilder buffer;
    OutputStream redirect;

    public StopReader(InputStream is) {
        this.is = is;
        redirect = null;
    }

    public void setRedirectionStream(OutputStream forward) {
        this.redirect = forward;
    }

    public boolean endsWith(String who, String[] any){
        for (String s : any) if (who.endsWith(s)) return true;
        return false;
    }

    public String readUntil(String... words) {
        try {
            buffer = new StringBuilder();
            int c;
            while (!endsWith(buffer.toString(),words) && (c = is.read()) >= 0) {
                buffer.append((char) c);
                if (redirect != null) {
                    redirect.write((char) c);
                }
            }

            return buffer.toString();
        } catch (IOException ex) {
            Logger.getLogger(StopReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * read until matches a lines by the given pattern
     * @param pattern
     * @param lines - number of lines to match
     *
     * for example if the regex is \nXXX\n
     * then we apply this method with the given pattern and lines = 3 because only 3 lines together will match the regex
     *
     * @return
     */
    public String readUntilLineRegex(Pattern pattern, int lines){
        int eoli;
        try {
            StringBuilder allbuff = new StringBuilder();
            buffer = new StringBuilder();
            int c;
            while (! pattern.matcher(buffer.toString()).matches() && (c = is.read()) >= 0) {
                buffer.append((char) c);
                allbuff.append((char) c);
                if (c == '\n')
                    lines--;
                if (lines < 0){
                    eoli = buffer.toString().indexOf("\n");
                    buffer.delete(0, eoli+1);
                    lines = 0;
                }

                if (redirect != null) {
                    redirect.write((char) c);
                }
            }

            return allbuff.toString();
        } catch (IOException ex) {
            Logger.getLogger(StopReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * return true if the given string can be matched using any of the given patterns
     * @param patterns
     * @param str
     * @return
     */
    private boolean orMatch(Pattern[] patterns, String str){
        for (Pattern p : patterns){
            if (p.matcher(str).matches()) return true;
        }

        return false;
    }

    /**
     * read until matches a lines by the given pattern
     * @param pattern
     * @param lines - number of lines to match
     *
     * for example if the regex is \nXXX\n
     * then we apply this method with the given pattern and lines = 3 because only 3 lines together will match the regex
     *
     * @return
     */
    public String readUntilLineRegex(int lines, Pattern... orPatterns){
        int eoli;
        try {
            StringBuilder allbuff = new StringBuilder();
            buffer = new StringBuilder();
            int c;
            while (! orMatch(orPatterns, buffer.toString()) && (c = is.read()) >= 0) {
                buffer.append((char) c);
                allbuff.append((char) c);
                if (c == '\n')
                    lines--;
                if (lines < 0){
                    eoli = buffer.toString().indexOf("\n");
                    buffer.delete(0, eoli+1);
                    lines = 0;
                }

                if (redirect != null) {
                    redirect.write((char) c);
                }
            }

            return allbuff.toString();
        } catch (IOException ex) {
            Logger.getLogger(StopReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String readToEOF() {
        try {
            buffer = new StringBuilder();
            int c;
            while ((c = is.read()) >= 0) {
                buffer.append((char) c);
                if (redirect != null) {
                    redirect.write((char) c);
                }
            }

            return buffer.toString();
        } catch (IOException ex) {
            Logger.getLogger(StopReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
