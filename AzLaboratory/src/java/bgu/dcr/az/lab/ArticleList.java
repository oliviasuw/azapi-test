/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Articles;
import bgu.dcr.az.lab.db.DBManager;
import java.sql.SQLException;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class ArticleList {
    
    
    public List<Articles> articles(int amount) throws SQLException {
        List<Articles> ans = DBManager.UNIT.getLastArticles(1,amount);
        return ans;
        
    }
    
    public String title(Articles p){
        return p.getTitle();
    }
    
    public String user(Articles p){
        return p.getUsers().getName();
    }
    
    public String date(Articles p){
        return p.getCreationDate().toString();
    }
    
    public String content(Articles p){
        return new String(p.getContent());
    }

}
