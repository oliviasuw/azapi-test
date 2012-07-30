/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Administrator
 */
public class DBManager {
    
    private static String TESTING_DB_URL = "objectdb:/az/azlab.tmp;drop";
    private static String DB_URL = "objectdb:/az/azlab.odb;"; 

    private EntityManagerFactory emf;

    public DBManager() {
//        com.objectdb.Enhancer.enhance("bgu.cyber.clfc.api.entities.*,bgu.cyber.clfc.boundaries.*");
        //System.setProperty("objectdb.home", SystemUtil.pwd().getAbsolutePath());
        emf = Persistence.createEntityManagerFactory(TESTING_DB_URL);
//        emf = Persistence.createEntityManagerFactory(DB_URL);
    }
    
    /**
     * @return new EntityManager, dont forget to close it when you done using it!
     */
    public EntityManager newEM(){
        return emf.createEntityManager();
    }
        
    public void save(Object o){
        EntityManager em = newEM();
        em.getTransaction().begin();
        
        em.persist(o);
        
        em.getTransaction().commit();
        em.close();
    }

    public void save(List all){
        EntityManager em = newEM();
        em.getTransaction().begin();
        
        for (Object a : all){
            em.persist(a);
        }
        
        em.getTransaction().commit();
        em.close();
    }
    
    public <T> List<T> loadAll(Class<T> type) {
        EntityManager em = newEM();
        TypedQuery<T> q = em.createQuery("select c from " + type.getSimpleName() + " c", type);
        List<T> list = q.getResultList();
        em.close();
        return list;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("I DEAD!");
    }
    
    
}
