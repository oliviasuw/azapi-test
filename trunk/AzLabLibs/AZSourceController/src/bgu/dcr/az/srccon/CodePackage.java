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
public class CodePackage {

    private static final String PATH_AGENTS = "/src/ext/sim/agents/";
    private static final String PATH_MODULES = "/src/ext/sim/modules/";
    private static final String PATH_TOOLS = "/src/ext/sim/tools/";
    private List<Code> sourceFiles;
    private List<CodeLib> externalLibreries;
    private File tmpFolder;
    private User user;

    public static CodePackage createPackage(User user, File zippedPackage, TempFolderGenerator tempf, DBManager db) throws IOException {
        File tmpFolder = tempf.generateTempFolder();
        Files.unzip(zippedPackage, tmpFolder);
        return parseDirectory(tmpFolder);
    }

    private static CodePackage parseDirectory(File tmpFolder) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private CodePackage(File tmpFolder, User user) {
        this.sourceFiles = null;
        this.externalLibreries = null;
        this.tmpFolder = tmpFolder;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public List<CodeLib> getExternalLibreries() {
        return externalLibreries;
    }

    public List<Code> getSourceFiles() {
        return sourceFiles;
    }

    public File getTmpFolder() {
        return tmpFolder;
    }

    /**
     *
     * @param rootPath - where to save the codePackage
     * @param tempf - folder generator
     * @param db - the db object to use in order to save the the codes and libs
     * @throws IOException
     */
    public void saveToDB(String rootPath, TempFolderGenerator tempf, DBManager db) throws IOException {
        rootPath = tempf.generateSubFolder(rootPath).getAbsolutePath();

        try {
            for (CodeLib clib : this.getExternalLibreries()) {
                File from = clib.getLocationOnDisk();
                File to = new File(rootPath + "/lib/" + from.getName());
                Files.copy(from, to);
                clib.setLocationOnDisk(to);
            }


            Set<Code> codes = new HashSet<>();
            findAllCodes(this.getSourceFiles(), codes);

            for (Code c : codes) {
                File from = c.getLocationOnDisk();
                File to = new File(getPathByType(rootPath, from.getName(), c.getType()));
                Files.copy(from, to);
                c.setLocationOnDisk(to);
            }

            db.save(codes, this.getExternalLibreries());
        } finally{
            Files.delete(tmpFolder);
        }
    }

    /**
     * generates the path for the given fileName according to its type
     * @param rootPath - the prefix of the path
     * @param fileName - the name of the file
     * @param type - the type of the file
     * @return  - path for the given fileName according to its type 
     * i.e. rootPath/[generated path from type]/fileName 
     */
    private String getPathByType(String rootPath, String fileName, CodeType type) {
        
        switch (type) {
            case AGENT:
                return rootPath + PATH_AGENTS + fileName;
            case TOOL:
                return rootPath + PATH_TOOLS + fileName;
            default:
                return rootPath + PATH_MODULES + fileName;
        }

    }

    /**
     * recursively collects all codes
     * @param sourceFiles - the initial list of codes
     * @param codes - map to store all the collected codes
     */
    private void findAllCodes(List<Code> sourceFiles, Set<Code> codes) {
        for (Code c : sourceFiles) {
            if (!codes.contains(c)) {
                codes.add(c);
                findAllCodes(c.getDependencies(), codes);
            }
        }
    }
}
