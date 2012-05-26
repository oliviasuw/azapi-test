/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.db;

import bgu.dcr.az.lab.data.Articles;
import bgu.dcr.az.lab.data.Users;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author kdima85
 */
public enum DBManager {

    UNIT;

    public void init() {
    }

    
    public List<Articles> getLastArticles(int pageNumber,int pageSize) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        //Transaction tx = s.beginTransaction();
        Criteria cr = s.createCriteria(Articles.class).addOrder(Order.desc("creationDate"));
        cr.setFirstResult(pageSize*(pageNumber-1));
        
            cr.setMaxResults(pageSize);
        

        List results = cr.list();

        return results;
    }
    
    public int countAllArticles(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        //Transaction tx = s.beginTransaction();
        Criteria cr = s.createCriteria(Articles.class).setProjection(Projections.rowCount());
        Number ans = (Number) cr.uniqueResult();
        return ans.intValue();
    }

    public Users isVerifiedUserCredentials(String email, String password) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Criteria cr = s.createCriteria(Users.class).add(Restrictions.eq("email", email)).add(Restrictions.eq("password", password));

        List results = cr.list();
        if (results.isEmpty()) {
            return null;
        }
        return (Users) results.get(0);
    }

    
    public void registerNewUser(Users u){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        s.save(u);
        tx.commit();
    }
    public List<Users> getAllUsers() {

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        List results = s.createCriteria(Users.class).list();


        tx.commit();
        return results;
    }

    //----------Users--------------------------//
    //----------Posts--------------------------//
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DBManager.UNIT.init();
        //         DBManager.UNIT.getLastArticles(5);
        List<Users> ans = DBManager.UNIT.getAllUsers();
        for (Users u : ans) {
            System.out.println("u = " + u.getName());
        }

        List<Articles> ans1 = DBManager.UNIT.getLastArticles(1,10);
        for (Articles u : ans1) {
            System.out.println("a = " + u.getTitle());
        }


    }
}
