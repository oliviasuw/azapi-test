/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.srccon;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeLib;
import bgu.dcr.az.db.ent.CodeType;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.srccon.util.Files;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kdima85
 */
public class SourceHandler {

    private static final String PATH_AGENTS = "/src/ext/sim/agents/";
    private static final String PATH_MODULES = "/src/ext/sim/modules/";
    private static final String PATH_TOOLS = "/src/ext/sim/tools/";
    TempFolderGenerator tempf;
    DBManager db;

    public SourceHandler(TempFolderGenerator tempf, DBManager db) {
        this.tempf = tempf;
        this.db = db;
    }

    /**
     * will save the un-zipped file to generated temp folder will parse the
     * un-zipped package return analyzed Object of all the enteties in the
     * package
     *
     * @param zipped
     */
    public CodePackage parsePackage(User user, File zippedPackage) throws IOException {
        File tmpFolder = tempf.generateTempFolder();
        Files.unzip(zippedPackage, tmpFolder);
        return parseDirectory(tmpFolder);
    }

    private CodePackage parseDirectory(File tmpFolder) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
}
