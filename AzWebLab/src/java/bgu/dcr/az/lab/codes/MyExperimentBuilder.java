/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.codes;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.lab.FileSystem;
import bgu.dcr.az.lab.exp.UnRecognizedUserException;
import bgu.dcr.az.lab.util.Files;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kdima85
 */
public class MyExperimentBuilder {

    FileSystem fs;

    public MyExperimentBuilder(FileSystem fs) {
        this.fs = fs;
    }

    public void build(List<Code> codes, File toDir) throws IOException {
        toDir.mkdirs();
        File libDir = new File(toDir.getAbsolutePath() + "/lib");
        libDir.mkdirs();


        for (Code code : codes) {
            copyCode(code, toDir, libDir);

        }
    }

    private void copyCode(Code c, File copyToDir, File libDir) throws IOException {
        
        for (String f : c.getDependencies()) {
            System.out.println("analyzing the file: " + f);
            try {
                if (f.endsWith(".jar")) {

                    String to = libDir.getAbsolutePath() + "/" + new File(f).getName();
                    System.out.println("it is a jar, copying to: " + to);
                    Files.copy(f, to);

                } else {

                    //the prefix of the TO location
                    String preffix = copyToDir.getAbsolutePath();

                    //finding the directory path starting from src
                    //the dependencie is located in the user folder
                    String userFolder = fs.getUserFolder(c.getAuthor()).getAbsolutePath();
                    //from the original location substract the user folder path
                    String suffix = f.substring(userFolder.length());
                    //find the first 'src'
                    suffix = suffix.substring(suffix.indexOf("src"));
                    String to = preffix + "/" + suffix;
                    Files.copy(f, to);
                    System.out.println("it is not a jar, copying to: " + to);
                }
            } catch (UnRecognizedUserException ex) {
                throw new IOException("There is no user assigned to the Code", ex.getCause());
            }
        }

    }

    public static void main(String[] args) throws IOException {
        DBManager db = new DBManager();
        FileSystem fs = new FileSystem();
        MyExperimentBuilder exb = new MyExperimentBuilder(fs);
        List<Code> codes = db.loadAll(Code.class);
        Code c = codes.get(0);
        codes = new LinkedList<Code>();
        codes.add(c);
        exb.build(codes, new File("c:\\az\\output"));
    }
}
