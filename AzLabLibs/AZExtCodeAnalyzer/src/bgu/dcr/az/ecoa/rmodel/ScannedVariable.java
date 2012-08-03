/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ecoa.rmodel;

import java.io.Serializable;

/**
 *
 * @author Inka
 */
public class ScannedVariable implements Serializable {

    public String name;
    public String type;
    public String defaultValue;
    public String description;

    public ScannedVariable(String name, String type, String defaultValue, String description) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "ScannedVariable{" + "name=" + name + ", type=" + type + ", defaultValue=" + defaultValue + ", description=" + description + '}';
    }
}
