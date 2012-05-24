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
public class Article {
    String title;
    String userName;
    Date published;

    public Article(String title, String userName, Date published) {
        this.title = title;
        this.userName = userName;
        this.published = published;
    }
    
    
}
