/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.codep;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Inka
 */
public class AzSourcePackScanner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Map<String, List<File>> toolsLocation = new HashMap<String, List<File>>();

        LinkedList<File> dirsToRead = new LinkedList<File>();
        dirsToRead.add(new File("test-data/src/ext/sim/tools"));
        while (!dirsToRead.isEmpty()) {
            for (File f : dirsToRead.remove().listFiles()) {
                if (f.isDirectory()) {
                    dirsToRead.add(f);
                } else {
                    if (f.getName().endsWith(".java")) {
                        List<String> list = Scanner.extractClasses(f);
                        for (String cls : list) {
                            List<File> l = toolsLocation.get(cls);
                            if (l == null) {
                                l = new LinkedList<File>();
                                toolsLocation.put(cls, l);
                            }

                            l.add(f);
                        }
                    }
                }
            }
        }

        System.out.println("Got: " + Arrays.toString(toolsLocation.entrySet().toArray()));


        dirsToRead.add(new File("test-data/src/ext/sim/agents"));
        dirsToRead.add(new File("test-data/src/ext/sim/modules"));
        dirsToRead.add(new File("test-data/src/ext/sim/tools"));
        while (!dirsToRead.isEmpty()) {
            for (File f : dirsToRead.remove().listFiles()) {
                if (f.isDirectory()) {
                    dirsToRead.add(f);
                } else {
                    if (f.getName().endsWith(".java")) {
                        Set<File> list = Scanner.extractDependencies(f, toolsLocation);
                        System.out.println("Class: " + f.getName() + " depends on: " + Arrays.toString(list.toArray()));
                        System.out.println("Description of file: " + f);
                        System.out.println(Scanner.extractClassDescription(f));
                    }
                }
            }
        }

    }
}
