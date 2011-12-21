/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author bennyl
 */
public class Var {
    public String display;
    public String value;
    public String type;
    public String description;

    public Var(String name, String value, String type, String description) {
        this.display = name;
        this.value = value;
        this.type = type;
        this.description = description;
    }

    
}
