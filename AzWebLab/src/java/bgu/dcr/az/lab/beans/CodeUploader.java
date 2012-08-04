/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.db.ent.UserRole;
import bgu.dcr.az.lab.FileSystem;
import bgu.dcr.az.lab.codes.PackageReadFailedException;
import bgu.dcr.az.lab.codes.SourcePackage;
import bgu.dcr.az.lab.exp.UnRecognizedUserException;
import bgu.dcr.az.lab.util.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Inka
 */
@ManagedBean(name = "codeUploader")
@ViewScoped
public class CodeUploader {

    public static final String AGENTS_PATH = "\\src\\ext\\sim\\agents";
    public static final String MODULES_PATH = "\\src\\ext\\sim\\modules";
    public static final String TOOLS_PATH = "\\src\\ext\\sim\\tools";
    @ManagedProperty("#{fileSystem}")
    FileSystem fs;
    @ManagedProperty("#{login}")
    private Login login;
    @ManagedProperty("#{dbManager}")
    private DBManager db;
    private UploadedFile file;
    private List<RowCode> classes;
    private RowCode[] selectedClasses;
    private HashMap<RowCode, Object> rowCodeToObject;
    private SourcePackage sp;

    public UploadedFile getFile() {
        return file;
    }

    public List<RowCode> getClasses() {
        return classes;
    }

    public RowCode[] getSelectedClasses() {
        return selectedClasses;
    }

    public HashMap<RowCode, Object> getRowCodeToCode() {
        return rowCodeToObject;
    }

    public void setClasses(List<RowCode> classes) {
        this.classes = classes;
    }

    public void setSelectedClasses(RowCode[] selectedClasses) {
        this.selectedClasses = selectedClasses;
    }

    public void setRowCodeToCode(HashMap<RowCode, Object> rowCodeToCode) {
        this.rowCodeToObject = rowCodeToCode;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Login getLogin() {
        return login;
    }

    public FileSystem getFs() {
        return fs;
    }

    public void setFs(FileSystem fs) {
        this.fs = fs;
    }

    public DBManager getDb() {
        return db;
    }

    public void setDb(DBManager db) {
        this.db = db;
    }

    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("event is " + event.toString());
        if (event.getFile() != null) {
            File tmpFolder = null;
            try {
                tmpFolder = fs.getTemporaryFolder();
                File packageZip = Files.persist(tmpFolder, event.getFile().getFileName(), event.getFile().getContents());
                User uploader = this.login.getUser();
//                User uploader = new User("email", "nick", "pass", "desc", UserRole.DCR);

                this.sp = new SourcePackage(packageZip, fs, uploader);
                fillUserCodesFromSourcePackage();


            } catch (IOException ex) {
                Logger.getLogger(CodeUploader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PackageReadFailedException ex) {
                Logger.getLogger(CodeUploader.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }

    private void fillUserCodesFromSourcePackage() {
        this.classes = new LinkedList<RowCode>();
        this.rowCodeToObject = new HashMap<RowCode, Object>();
        int idx = 0;
        for (Code code : sp.getCodeInPackage()) {
            RowCode rCode = new RowCode(idx++, code.getName(), code.getType().toString());
            classes.add(rCode);
            rowCodeToObject.put(rCode, code);
        }
    }

    public void saveCode() {
        try {
            this.sp.getCodeInPackage().clear();
            for (RowCode c : selectedClasses) {
                Code code = (Code) this.rowCodeToObject.get(c);
                this.sp.getCodeInPackage().add(code);
            }

            this.sp.saveToDB(this.db);

        } catch (UnRecognizedUserException ex) {
            Logger.getLogger(CodeUploader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CodeUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
