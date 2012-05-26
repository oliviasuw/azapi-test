/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.data.Articles;
import bgu.dcr.az.lab.data.Users;
import bgu.dcr.az.lab.db.DBManager;
import com.sun.faces.context.SessionMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@SessionScoped
public class Search {

    private static int PAGE_SIZE = 2;
    private Articles viewed;
    private long currentPage = 1;
    private int totalPages = 1;
    List<Articles> results;
    private WatchingType watchingNow;


    
    public Search() {
        watchingNow = WatchingType.recent;
        results = DBManager.UNIT.getLastArticles(1, PAGE_SIZE);
        viewed = results.get(0);
        totalPages = calcTotalPages();
        
    }

    public Articles getViewed() {
        return viewed;
    }

    public List<Articles> getResults() {
        return results;
    }

    public void recent(Long pageNum) {
        results = DBManager.UNIT.getLastArticles(pageNum.intValue(), PAGE_SIZE);
        watchingNow = WatchingType.recent;

    }

    public void tags(Long pageNum) {
        results = DBManager.UNIT.getLastArticles(pageNum.intValue(), PAGE_SIZE);
        watchingNow = WatchingType.tags;

    }

    public void comments(Long pageNum) {
        results = DBManager.UNIT.getLastArticles(pageNum.intValue(), PAGE_SIZE);
        watchingNow = WatchingType.comments;

    }

    public void popular(Long pageNum) {
        results = DBManager.UNIT.getLastArticles(pageNum.intValue(), PAGE_SIZE);
        watchingNow = WatchingType.popular;

    }

    public String getWatchingNow() {
        return watchingNow.toString();
    }

    public void changeViewed(int id) {
        System.out.println("Id is" + id);
        viewed = findById(id);
    }

    public Articles findById(int id) {
        for (Articles article : results) {
            if (article.getId() == id) {
                return article;
            }
        }
        return viewed;
    }

    public String getViewdContent() {
        if (viewed == null) {
            return "";
        }

        return new String(viewed.getContent());
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    private int calcTotalPages() {
        int total = DBManager.UNIT.countAllArticles();
        if (total % 2 != 0) {
            total++;
        }
        return total / PAGE_SIZE;
    }

    public String getCurrentUser() {
        
        Users x = (Users) SessionManager.getFromSessionMap(SessionManager.CURRENT_USER);
        return x==null? "no user": x.getName();
    }

    
   public String writeSomething(){
       return "WriteArticle";
   }
    
    
    
    
    
    public void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            handleNewCurrentPage();
        }
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            handleNewCurrentPage();
        }
    }
    
    public void handleNewCurrentPage(){
        switch (watchingNow) {
                case comments:
                    comments(currentPage);
                    break;
                case popular:
                    popular(currentPage);
                    break;
                case recent:
                    recent(currentPage);
                    break;
                case tags:
                    tags(currentPage);
                    break;
                default:
                    recent(currentPage);   
            }
    }
    
    
    public void changeStateTo(String state){
        this.currentPage = 1;
        this.watchingNow = WatchingType.valueOf(state);
        handleNewCurrentPage();
    }

    
    
    
    public static enum WatchingType {

        recent("recent"),
        comments("comments"),
        tags("tags"),
        popular("popular");
        String str;

        private WatchingType(String s) {
            str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
}