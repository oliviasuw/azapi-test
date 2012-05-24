/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class ArticleList {
    
    private Article[] articles = {new Article("An article abour DCR", "Student A", new Date()),
                            new Article("An article abour Asynchronous execution", "Student B", new Date()),
                            new Article("An article abour DPOP", "Student C", new Date())};

    public Article[] getArticles() {
        return articles;
    }
    
    public String title(Article p){
        return p.title;
    }
    
    public String user(Article p){
        return p.userName;
    }
    
    public String date(Article p){
        return p.published.toString();
    }
    
    

}
