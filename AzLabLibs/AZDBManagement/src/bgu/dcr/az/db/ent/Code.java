/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import bgu.dcr.az.db.DBManager;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

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
    private List<String> dependencies;
    private String name;
    private String locationOnDisk;
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
        dependencies = new LinkedList<String>();
        numberOfDownloads = 0;
        safe = false;
        rating = 0;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public List<VariableDecleration> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableDecleration> variables) {
        this.variables = variables;
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

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
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

    public List<String> getDependencies() {
        return dependencies;
    }

    public String getName() {
        return name;
    }

    public String getLocationOnDisk() {
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

    public static List<Code> getAllCodesByType(DBManager db, CodeType type) {
        List<Code> ans = new LinkedList<>();
        EntityManager em = db.newEM();
        TypedQuery<Code> qCode = em.createQuery("select c from Code c where c.type = :type", Code.class);
        qCode.setParameter("type", type);
        ans.addAll(qCode.getResultList());
        em.close();
        return ans;
    }

    public void addVariable(VariableDecleration var) {
        if (variables == null) {
            variables = new LinkedList<>();
        }
        variables.add(var);
    }

    @Override
    public String toString() {
        return "Code{" + "id=" + id + ", type=" + type + ", dependencies=" + dependencies + ", name=" + name + ", locationOnDisk=" + locationOnDisk + ", description=" + description + ", numberOfDownloads=" + numberOfDownloads + ", safe=" + safe + ", author=" + author + ", registeredName=" + registeredName + ", variables=" + variables + ", comments=" + comments + ", rating=" + rating + '}';
    }

}
