/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package azsourcecontroller;

import java.io.File;
import java.util.Random;

/**
 *
 * @author kdima85
 */
public class TempFolderGenerator {
    
    
    String rootPath;

    public TempFolderGenerator(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public synchronized File getTempFolder(){
        
        long tempFolderSuffix = System.currentTimeMillis();
        File f = new File(rootPath+ tempFolderSuffix);
        f.mkdir();
        return f;
    }
}
