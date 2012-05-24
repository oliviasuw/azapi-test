/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.util.Date;

/**
 *
 * @author Inna
 */
public class Post {
    String title;
    String content;
    Date published;

    public Post(String title, String content, Date published) {
        this.title = title;
        this.content = content;
        this.published = published;
    }
    
    
}
