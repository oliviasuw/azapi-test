/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import bgu.dcr.az.db.DBManager;
import java.io.Serializable;
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
public class Experiment implements Serializable {

    private @Id
    @GeneratedValue
    long id = 0;
    private String name;
    private String locationOnDisk;
    private String description;
    private User owner;
    private List<Comment> comments;
    private boolean publicExp;
    private String results;

    public Experiment() {
    }

    public Experiment(String name, String description, User owner, String results) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.comments = new LinkedList<>();
        this.publicExp = false;
        this.results = results;
        this.locationOnDisk = null;
    }

    public long getId() {
        return id;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getResults() {
        return results;
    }

    public void setPublicExp(boolean publicExp) {
        this.publicExp = publicExp;
    }

    public boolean isPublicExp() {
        return publicExp;
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

    public static List<Experiment> getAllPublicExperiments(DBManager db) {
        List<Experiment> ans = new LinkedList<>();
        EntityManager em = db.newEM();
        TypedQuery<Experiment> qCode = em.createQuery("select e from Experiment e where e.publicExp = true", Experiment.class);
        ans.addAll(qCode.getResultList());
        em.close();
        return ans;
    }
}
