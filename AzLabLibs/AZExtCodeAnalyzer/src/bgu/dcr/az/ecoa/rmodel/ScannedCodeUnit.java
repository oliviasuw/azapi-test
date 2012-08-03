/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ecoa.rmodel;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Inka
*/
public class ScannedCodeUnit implements Serializable {

    public String type;
    public List<File> dependencies;
    public File locationOnDisk;
    public String description;
    public String registeredName;
    public List<ScannedVariable> variables;

    public ScannedCodeUnit() {
        dependencies = new LinkedList<File>();
    }

    @Override
    public String toString() {
        return "ScannedCodeUnit{" + "type=" + type + ", dependencies=" + Arrays.toString(dependencies.toArray()) + ", locationOnDisk=" + locationOnDisk + ", description=" + description + ", registeredName=" + registeredName + ", variables=" + Arrays.toString(variables.toArray()) + '}';
    }
    
    
}
