/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import bgu.dcr.az.db.DBManager;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.jdo.annotations.Unique;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author Inka
 */
@Entity
public class User implements Serializable {

    private @Unique
    String email;
    private String nickName;
    private String password;
    private String description;
    private UserRole uRole;
    private @Id
    @GeneratedValue
    long id = 0;

    protected User() {
    }

    public User(String email, String nickName, String password, String description, UserRole role) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.description = description;
        this.uRole = role;
    }

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public UserRole getRole() {
        return uRole;
    }

    public long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRole(UserRole role) {
        this.uRole = role;
    }

    public static List<User> getAllUsers(DBManager db) {
        return db.loadAll(User.class);
    }

    public static User getByEmail(String email, DBManager db) {
        EntityManager em = db.newEM();
        TypedQuery<User> q = em.createQuery("select c from User c where c.email = :email", User.class);
        q.setParameter("email", email);
        try {
            User r = q.getSingleResult();
            return r;
        } catch (NoResultException e) {
            System.out.println(e);
        } finally {
            em.close();
        }
        return null;
    }

    public List<Code> getAllUploadedCodes(DBManager db) {
        List<Code> ans = new LinkedList<>();
        EntityManager em = db.newEM();
        TypedQuery<Code> qCode = em.createQuery("select c from Code c where c.author = :user", Code.class);
        qCode.setParameter("user", this);
        ans.addAll(qCode.getResultList());
        em.close();
        return ans;
    }
}
