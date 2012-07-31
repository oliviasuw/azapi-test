/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Inka
 */
@Entity
public class Code implements Serializable {

    private @Id
    @GeneratedValue
    long id = 0;
    private CodeType type;
    private List<File> dependencies;
    private String name;
    private File locationOnDisk;
    private String description;
    private int numberOfDownloads;
    private boolean safe;
    private User author;
    private String registeredName;
    private List<VariableDecleration> variables;
    private List<Comment> comments;
    private int rating;

    public Code() {
        comments = new LinkedList<Comment>();
        dependencies = new LinkedList<File>();
        numberOfDownloads = 0;
        safe = false;
        rating = 0;
    }

    public void setType(CodeType type) {
        this.type = type;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public CodeType getType() {
        return type;
    }

    public int getRating() {
        return rating;
    }

    public void setDependencies(List<File> dependencies) {
        this.dependencies = dependencies;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setLocationOnDisk(File locationOnDisk) {
        this.locationOnDisk = locationOnDisk;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumberOfDownloads(int numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public long getId() {
        return id;
    }

    public List<File> getDependencies() {
        return dependencies;
    }

    public String getName() {
        return name;
    }

    public File getLocationOnDisk() {
        return locationOnDisk;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfDownloads() {
        return numberOfDownloads;
    }

    public boolean isSafe() {
        return safe;
    }

    public User getAuthor() {
        return author;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
