/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Inka
 */
@Embeddable
public class VariableDecleration implements Serializable {

    private String name;
    private String type;
    private String defaultValue;
    private String description;

    protected VariableDecleration() {
    }

    public VariableDecleration(String name, String type, String defaultValue, String description) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "VariableDecleration{" + "name=" + name + ", type=" + type + ", defaultValue=" + defaultValue + ", description=" + description + '}';
    }
    
    
}
