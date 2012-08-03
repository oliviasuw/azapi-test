/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.spr;

import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.utils.Files;
import java.io.File;

/**
 *
 * @author Administrator
 */
public class FileSystem {
    public static final String BASE_FOLDER = "/az";
    
    public File getUserFolder(User u) throws UnRecognizedUserException{
        if (u.getId() == 0) throw new UnRecognizedUserException("user " + u + " not recognized");
        File f = new File(BASE_FOLDER + "/" + u.getId());
        f.mkdirs();
        return f;
    }
    
    public File getTemporaryFolder(){
        while (true){
            File f = new File(BASE_FOLDER + "/tmp/" + Math.random());
            if (!f.exists()){
                f.mkdirs();
                return f;
            }
        }
    }
    
    public void deleteAllTempFiles(){
        Files.delete(new File(BASE_FOLDER + "/tmp"));
    }
    
    public File getRootFolder(){
        File f = new File(BASE_FOLDER + "/root");
        if (!f.exists()){
            f.mkdirs();
        }
        
        return f;
    }
}
