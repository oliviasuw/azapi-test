/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static String DB_SERVER_URL = "objectdb://localhost:6136/azlab.tmp;";
    private EntityManagerFactory emf;

    public DBManager() {
//        com.objectdb.Enhancer.enhance("bgu.cyber.clfc.api.entities.*,bgu.cyber.clfc.boundaries.*");
        //System.setProperty("objectdb.home", SystemUtil.pwd().getAbsolutePath());

        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.user", "admin");
        properties.put("javax.persistence.jdbc.password", "admin");
        emf = Persistence.createEntityManagerFactory(DB_SERVER_URL, properties);
//        emf = Persistence.createEntityManagerFactory(DB_URL);
    }

    /**
     * @return new EntityManager, dont forget to close it when you done using
     * it!
     */
    public EntityManager newEM() {
        return emf.createEntityManager();
    }

    public void update(Object o) {
        EntityManager em = newEM();
        em.getTransaction().begin();


        em.merge(o);
        //em.persist(o);

        em.getTransaction().commit();
        em.close();
    }

    public void save(Object o) {
        EntityManager em = newEM();
        em.getTransaction().begin();

        em.persist(o);

        em.getTransaction().commit();
        em.close();
    }

    public void saveAll(Iterable... all) {
        EntityManager em = newEM();
        em.getTransaction().begin();

        for (Iterable aa : all) {
            for (Object a : aa) {
                em.persist(a);
            }
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
}
