/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Inka
 */
@Entity
public class Experiment implements Serializable {
    private @Id @GeneratedValue long id;
    private String name;
    private String locationOnDisk;
    private String description;
    private User owner;
    private List<Comment> comments;

    public String getName() {
        return name;
    }

    public String getLocationOnDisk() {
        return locationOnDisk;
    }

    public String getDescription() {
        return description;
    }

    public User getOwner() {
        return owner;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocationOnDisk(String locationOnDisk) {
        this.locationOnDisk = locationOnDisk;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    
}
