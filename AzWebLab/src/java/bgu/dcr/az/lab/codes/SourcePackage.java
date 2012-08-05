/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.codes;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeType;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.db.ent.VariableDecleration;
import bgu.dcr.az.ecoa.rmodel.ScannedCodeUnit;
import bgu.dcr.az.ecoa.rmodel.ScannedVariable;
import bgu.dcr.az.lab.FileSystem;
import bgu.dcr.az.lab.exp.UnRecognizedUserException;
import bgu.dcr.az.lab.util.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author Administrator
 */
public class SourcePackage {

    private List<Code> codeInPackage;
    private File location;
    FileSystem fs;
    User user;

    public SourcePackage(File packageZip, FileSystem fs, User uploader) throws PackageReadFailedException {
        this(fs.getSourceAnalyzerAntScript(), packageZip, fs, uploader);
    }

    public SourcePackage(File antScript, File packageZip, FileSystem fs, User uploader) throws PackageReadFailedException {
        this.fs = fs;
        this.location = packageZip.getAbsoluteFile().getParentFile();
        System.out.println("Location is: " + location.getAbsolutePath());
        this.user = uploader;
        Project p = new Project();
        p.setUserProperty("ant.file", fs.getSourceAnalyzerAntScript().getAbsolutePath());
        p.setUserProperty("azlib", fs.getAzLibFolder().getAbsolutePath());
        p.setUserProperty("sanlib", fs.getSourceAnalyzerLibFolder().getAbsolutePath());
        p.setUserProperty("package.zip", packageZip.getAbsolutePath());
        p.setUserProperty("base.dir", location.getAbsolutePath());
        p.setBaseDir(location);
        p.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference("ant.projectHelper", helper);
        helper.parse(p, antScript);
        p.executeTarget(p.getDefaultTarget());
        ObjectInputStream ois = null;

        try {
            FileInputStream fis = new FileInputStream(new File(location.getAbsolutePath() + "/out.txt"));
            ois = new ObjectInputStream(fis);
            List<ScannedCodeUnit> res = (List<ScannedCodeUnit>) ois.readObject();
            codeInPackage = new LinkedList<Code>();

            for (ScannedCodeUnit r : res) {
                Code code = new Code();
                code.setAuthor(uploader);
                code.setDependencies(listAbsoluts(r.dependencies));
                code.setDescription(r.description);
                code.setLocationOnDisk(r.locationOnDisk.getAbsolutePath());
                System.out.println("Location on disk is: " + r.locationOnDisk.getAbsolutePath());
                code.setName(r.locationOnDisk.getName());
                code.setRegisteredName(r.registeredName);
                code.setType(CodeType.byName(r.type));

                LinkedList<VariableDecleration> decs = new LinkedList<VariableDecleration>();
                for (ScannedVariable v : r.variables) {
                    decs.add(new VariableDecleration(v.name, v.type, v.defaultValue, v.description));
                }
                code.setVariables(decs);
                codeInPackage.add(code);
            }

        } catch (ClassNotFoundException ex) {
            throw new PackageReadFailedException("cannot parse package.", ex);
        } catch (IOException ex) {
            throw new PackageReadFailedException("cannot parse package.", ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(SourcePackage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     *
     * @param rootPath - where to save the codePackage
     * @param tempf - folder generator
     * @param db - the db object to use in order to save the the codes and libs
     * @throws IOException
     */
    public void saveToDB(DBManager db) throws IOException, UnRecognizedUserException {

        File userPackageFolder = fs.makeUserReandomFolder(this.user);
        File userLibFolder = fs.getUserLibFolder(user);
        try {

            for (Code c : this.codeInPackage) {
                copyCode(c, userPackageFolder, userLibFolder);
            }


            db.saveAll(this.codeInPackage);
        } finally {
            Files.delete(this.location);
        }
    }

    public List<Code> getCodeInPackage() {
        return codeInPackage;
    }

    public File getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "SourcePackage{" + "codeInPackage=" + Arrays.toString(codeInPackage.toArray()) + ", location=" + location + '}';
    }

    private void copyCode(Code c, File userPackageFolder, File userLibFolder) throws IOException {
        List<String> newDependencies = new LinkedList<String>();
        for (String f : c.getDependencies()) {
            System.out.println("analyzing the file: " + f);
            if (f.endsWith(".jar")) {
                String to = userLibFolder.getAbsolutePath() + "/" + new File(f).getName();
                System.out.println("it is a jar, copying to: " + to);
                Files.copy(f, to);
                newDependencies.add(to);
            } else {
                String preffix = userPackageFolder.getAbsolutePath();
                String suffix = f.substring(location.getAbsolutePath().length());
                String to = preffix + "/" + suffix;
                Files.copy(f, to);
                System.out.println("it is not a jar, copying to: " + to);
                newDependencies.add(to);
            }
        }

        //TODO: update the location on disk of the code
        String preffix = userPackageFolder.getAbsolutePath();
        String suffix = c.getLocationOnDisk().substring(location.getAbsolutePath().length());
        String to = preffix + "/" + suffix;

        c.setLocationOnDisk(to);
        c.setDependencies(newDependencies);
    }

    private List<String> listAbsoluts(List<File> dependencies) {
        LinkedList<String> ret = new LinkedList<String>();
        for (File d : dependencies) {
            ret.add(d.getAbsolutePath());
        }

        return ret;
    }
}
