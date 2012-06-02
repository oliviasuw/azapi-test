/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.db;

import bgu.dcr.az.lab.data.Articles;
import bgu.dcr.az.lab.data.Cpu;
import bgu.dcr.az.lab.data.Experiments;
import bgu.dcr.az.lab.data.Users;
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
public class DBManagerHibernate implements DBManagerIfc{
    
    @Override
    public List<Articles> getLastArticles(int pageNumber, int pageSize) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        //Transaction tx = s.beginTransaction();
        Criteria cr = s.createCriteria(Articles.class).addOrder(Order.desc("creationDate"));
        cr.setFirstResult(pageSize * (pageNumber - 1));

        cr.setMaxResults(pageSize);


        List results = cr.list();

        return results;
    }

    @Override
    public int countAllArticles() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        //Transaction tx = s.beginTransaction();
        Criteria cr = s.createCriteria(Articles.class).setProjection(Projections.rowCount());
        Number ans = (Number) cr.uniqueResult();
        return ans.intValue();
    }

    @Override
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

    @Override
    public void registerNewUser(Users u) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        s.save(u);
        tx.commit();
    }

    @Override
    public List<Users> getAllUsers() {

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        List results = s.createCriteria(Users.class).list();


        tx.commit();
        return results;
    }

    @Override
    public List<Cpu> getUserCpus(Users u) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Criteria cr = s.createCriteria(Cpu.class).add(Restrictions.eq("users", u));

        List results = cr.list();

        return results;
    }

    @Override
    public List<Experiments> getUserExperiments(Users u) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Criteria cr = s.createCriteria(Experiments.class).add(Restrictions.eq("users", u));

        List results = cr.list();

        return results;
    }

    @Override
    public void addNewExperiment(Experiments exp) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        s.save(exp);
        tx.commit();
    }


    
}
