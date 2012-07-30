/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.srccon;

import bgu.dcr.az.srccon.util.Files;
import java.io.File;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author kdima85
 */
public class TempFolderGenerator {

    private AtomicLong tempId = new AtomicLong(0);
    String tmpRootPath;

    public TempFolderGenerator(String tmpRootPath) {
        this.tmpRootPath = tmpRootPath;
    }

    /**
     * will generate a temp folder
     *
     * @return - the generated temp folder
     */
    public File generateTempFolder() {
        return generateSubFolder(tmpRootPath);

    }

    /**
     * will generate a folder inside the given path
     *
     * @param where - the path where to generate the subfolder
     * @return - the generated folder inside the given path
     */
    public File generateSubFolder(String where) {
        where = where.trim();
        if (!where.endsWith("/")) {
            where = where + "/";
        }

        File f = new File(where + tempId.incrementAndGet());

        while (!f.mkdirs()) {
            f = new File(where + tempId.incrementAndGet());
        }

        return f;
    }
    
    /**
     * will generate a folder inside the given path
     *
     * @param where - the path where to generate the subfolder
     * @return - the generated folder inside the given path
     */
    public File generateSubFolder(File where) {
        return generateSubFolder(where.getAbsolutePath());
    }
}
