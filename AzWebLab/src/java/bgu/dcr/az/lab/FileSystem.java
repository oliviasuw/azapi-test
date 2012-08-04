/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.lab.exp.UnRecognizedUserException;
import bgu.dcr.az.lab.util.Files;
import java.io.File;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Administrator
 */
@ApplicationScoped
@ManagedBean(eager = true)
public class FileSystem {

    public static final String BASE_FOLDER = "/az";

    public File getUserFolder(User u) throws UnRecognizedUserException {
        if (u.getId() == 0) {
            throw new UnRecognizedUserException("user " + u + " not recognized");
        }
        File f = new File(BASE_FOLDER + "/users/" + u.getId());
        f.mkdirs();
        return f;
    }

    public File getUserLibFolder(User u) throws UnRecognizedUserException{
        File f = new File (getUserFolder(u).getAbsolutePath() + "/lib");
        f.mkdirs();
        return f;
    }
    
    public File makeUserReandomFolder(User u) throws UnRecognizedUserException{
        return makeRandomFolderAt(getUserFolder(u));
    }
    
    private File makeRandomFolderAt(String from){
        while (true) {
            File f = new File(from + "/" + Math.random());
            if (!f.exists()) {
                System.out.println("returning temp folder: " + f.getAbsolutePath());
                f.mkdirs();
                return f;
            }
        }
    }
    
    private File makeRandomFolderAt(File from){
        while (true) {
            File f = new File(from.getAbsolutePath() + "/" + Math.random());
            if (!f.exists()) {
                f.mkdirs();
                return f;
            }
        }
    }
    
    
    
    public File getTemporaryFolder() {
        return makeRandomFolderAt(BASE_FOLDER + "/tmp/");
    }

    public void deleteAllTempFiles() {
        Files.delete(new File(BASE_FOLDER + "/tmp"));
    }

    public File getRootFolder() {
        File f = new File(BASE_FOLDER + "/root");
        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }

    public File getAzLibFolder() {
        return new File(getRootFolder().getAbsolutePath() + "/azlib");
    }

    private String getSourceAnalyzerFolderPath() {
        return getRootFolder().getAbsolutePath() + "/source-analyzer";
    }

    public File getSourceAnalyzerLibFolder() {
        return new File(getSourceAnalyzerFolderPath() + "/lib");
    }

    public File getSourceAnalyzerAntScript() {
        return new File(getSourceAnalyzerFolderPath() + "/package.xml");
    }

    public File getTestDataFolder() {
        return new File(BASE_FOLDER + "/test-data");
    }
}
