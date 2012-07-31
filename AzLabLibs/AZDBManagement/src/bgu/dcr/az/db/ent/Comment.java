/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Inka
 */
@Entity
public class Comment {

    private String title;
    private String body;
    private String author;
    private @Id
    @GeneratedValue
    long id = 0;

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
