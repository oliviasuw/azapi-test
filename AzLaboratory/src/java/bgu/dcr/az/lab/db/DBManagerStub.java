/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.db;

import bgu.dcr.az.lab.data.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kdima85
 */
public class DBManagerStub implements DBManagerIfc {

    private List<Users> users;
    private List<Articles> articles;
    private List<Cpu> cpus;
    private List<Experiments> experiments;
    private List<ArticlesTags> articleTags;
    private List<Tags> tags;
    private List<Comments> comments;

    public DBManagerStub() {
        this.users = new LinkedList<Users>();
        this.articles = new LinkedList<Articles>();
        this.cpus = new LinkedList<Cpu>();
        this.comments = new LinkedList<Comments>();
        this.tags = new LinkedList<Tags>();
        this.experiments = new LinkedList<Experiments>();
        this.articles = new LinkedList<Articles>();
        initData();
        
        
    }
    
    
    
    
    @Override
    public List<Articles> getLastArticles(int pageNumber, int pageSize) {
        int start = pageSize * (pageNumber - 1);
        int end = start + pageSize;
        List<Articles> ans = new LinkedList<Articles>();
        for(int i=start; i<end && i<articles.size(); i++){
            ans.add(articles.get(i));
        }
        
        return ans;
    }

    @Override
    public int countAllArticles() {
        return articles.size();
    }

    @Override
    public Users isVerifiedUserCredentials(String email, String password) {
        for(Users u: users){
            if (u.getEmail().equals(email) && u.getPassword().equals(password)){
                return u;
            }
        }
        
        return null;
    }

    @Override
    public void registerNewUser(Users u) {
        u.setId(users.size());
        users.add(u);
    }

    @Override
    public List<Users> getAllUsers() {
        return users;


    }

    @Override
    public List<Cpu> getUserCpus(Users u) {
        List<Cpu> ans = new LinkedList<Cpu>();
        for(Cpu c : cpus){
            if(c.getUsers().getId()==u.getId()){
                ans.add(c);
            }
        }
        return ans;
    }

    @Override
    public List<Experiments> getUserExperiments(Users u) {
        List<Experiments> ans = new LinkedList<Experiments>();
        for( Experiments exp : experiments){
            if(exp.getUsers().getId()==u.getId()){
                ans.add(exp);
            }
        }
        return ans;
    }

    @Override
    public void addNewExperiment(Experiments exp) {
        exp.setId(experiments.size());
        experiments.add(exp);
    }

    private void initData() {
        Users inna = new Users("inkago1@gmail.com", "1234", "Inna", new byte[0], true);
        inna.setId(0);
        Users dima = new Users("dima3685@gmail.com", "1234", "Dima", new byte[0], true);
        dima.setId(1);
        Users benny = new Users("benny.lutati@gmail.com", "1234", "Benny", new byte[0], true);
        benny.setId(2);
        users.add(inna);
        users.add(dima);
        users.add(benny);
        
        Articles article1 = new Articles(benny, "article 1 title", new Date(), 0, true);
        article1.setContent("article 1 content".getBytes());
        article1.setId(0);
        Articles article2 = new Articles(benny, "article 2 title", new Date(), 0, true);
        article2.setContent("article 1 content".getBytes());
        article2.setId(1);
        Articles article3 = new Articles(benny, "article 3 title", new Date(), 0, true);
        article3.setContent("article 1 content".getBytes());
        article3.setId(2);
        Articles article4 = new Articles(benny, "article 4 title", new Date(), 0, true);
        article4.setContent("article 1 content".getBytes());
        article4.setId(3);
        Articles article5 = new Articles(benny, "article 5 title", new Date(), 0, true);
        article5.setContent("article 1 content".getBytes());
        article5.setId(4);
        
        articles.add(article1);
        articles.add(article2);
        articles.add(article3);
        articles.add(article4);
        articles.add(article5);
        
        
        Cpu cpu1 = new Cpu(inna, "cpu1 data");
        Cpu cpu2 = new Cpu(inna, "cpu2 data");
        Cpu cpu3 = new Cpu(inna, "cpu3 data");
        Cpu cpu4 = new Cpu(dima, "cpu4 data");
        cpus.add(cpu1);
        cpus.add(cpu2);
        cpus.add(cpu3);
        cpus.add(cpu4);
        
        Experiments exp1 = new Experiments(inna, "exp1 data".getBytes());
        Experiments exp2 = new Experiments(inna, "exp2 data".getBytes());
        Experiments exp3 = new Experiments(inna, "exp3 data".getBytes());
        Experiments exp4 = new Experiments(inna, "exp4 data".getBytes());
        Experiments exp5 = new Experiments(inna, "exp5 data".getBytes());
        Experiments exp6 = new Experiments(dima, "exp6 data".getBytes());
        Experiments exp7 = new Experiments(benny, "exp7 data".getBytes());
        experiments.add(exp1);
        experiments.add(exp2);
        experiments.add(exp3);
        experiments.add(exp4);
        experiments.add(exp5);
        experiments.add(exp6);
        experiments.add(exp7);
    }

    
    public static void main(String[] args){
        DBManagerStub manager = new DBManagerStub();
        
        Users u = manager.isVerifiedUserCredentials("inkago1@gmail.com", "1234");
        System.out.println(manager.getUserCpus(u).size());
        
    }
}
