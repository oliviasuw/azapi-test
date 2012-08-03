/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.spr;

import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeType;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.db.ent.VariableDecleration;
import bgu.dcr.az.ecoa.rmodel.ScannedCodeUnit;
import bgu.dcr.az.ecoa.rmodel.ScannedVariable;
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
    public static final File PACKAGE_XML = new File("package.xml");
    
    private List<Code> codeInPackage;
    private File location;

    public SourcePackage(File packageZip, File azLib, User uploader) throws PackageReadFailedException {
        this.location = packageZip.getParentFile();
        
        File packageXml = new File(getClass().getResource("package.xml").toExternalForm());
        
        Project p = new Project();
        p.setUserProperty("ant.file", packageXml.getAbsolutePath());
        p.setUserProperty("azlib", azLib.getAbsolutePath());
        p.setBaseDir(location);
        p.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference("ant.projectHelper", helper);
        helper.parse(p, packageXml);
        p.executeTarget(p.getDefaultTarget());
        ObjectInputStream ois = null;
        
        try {
            FileInputStream fis = new FileInputStream(new File(location.getAbsolutePath() + "/out.txt"));
            ois = new ObjectInputStream(fis);
            List<ScannedCodeUnit> res = (List<ScannedCodeUnit>) ois.readObject();
            codeInPackage = new LinkedList<>();
            
            for (ScannedCodeUnit r : res){
                Code code = new Code();
                code.setAuthor(uploader);
                code.setDependencies(r.dependencies);
                code.setDescription(r.description);
                code.setLocationOnDisk(r.locationOnDisk);
                code.setName(code.getLocationOnDisk().getName());
                code.setRegisteredName(r.registeredName);
                code.setType(CodeType.byName(r.type));
                
                LinkedList<VariableDecleration> decs = new LinkedList<>();
                for (ScannedVariable v : r.variables){
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
    
    public static void main(String[] args) throws PackageReadFailedException{
        SourcePackage p = new SourcePackage(new File("temp/package.zip"), new File("azlib"), null);
        System.out.println(p);
    }
    
}
