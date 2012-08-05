/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.codes;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.db.ent.UserRole;
import bgu.dcr.az.lab.FileSystem;
import bgu.dcr.az.lab.exp.UnRecognizedUserException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author kdima85
 */
public class ApplicationStartup {

    private static final String CORE_USER_EMAIL = "core@dcr.ac.il";
    private static final String CORE_USER_PASSWORD = "pass";
    FileSystem fs;
    DBManager db;

    public ApplicationStartup(FileSystem fs, DBManager db) {
        this.fs = fs;
        this.db = db;
    }

    public void createAdminUser() {
        User u = new User(CORE_USER_EMAIL, "core", CORE_USER_PASSWORD, "core user of the system", UserRole.DCR);
        db.save(u);
    }

    public User getCoreUser() {
        return User.getByEmail(CORE_USER_EMAIL, db);
    }

    public void loadCoreFeatures() throws PackageReadFailedException, IOException, UnRecognizedUserException{
        SourcePackage spack = new SourcePackage(fs.getInitialSourceAnalyzerAntScript(), fs.getRootFolder(), fs, getCoreUser());
        spack.saveToDB(db);
    }
    
    public static void main(String[] args) throws PackageReadFailedException, IOException, UnRecognizedUserException {
        DBManager db = new DBManager();
        FileSystem fs = new FileSystem();
        ApplicationStartup appStart = new ApplicationStartup(fs, db);
//        appStart.createAdminUser();
        appStart.loadCoreFeatures();
        

    }
}
