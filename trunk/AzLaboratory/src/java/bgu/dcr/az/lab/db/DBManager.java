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

/**
 *
 * @author kdima85
 */
public enum DBManager implements DBManagerIfc{

    UNIT;


    private final static boolean DEBUG = true;
    private DBManagerIfc manager;
    public void init() {
        if (DEBUG){
            manager = new DBManagerStub();
        }else{
            manager = new DBManagerHibernate();
        }
    }

    @Override
    public List<Articles> getLastArticles(int pageNumber, int pageSize) {
            return manager.getLastArticles(pageNumber, pageSize);
        }

    @Override
    public int countAllArticles() {
        return manager.countAllArticles();
    }

    @Override
    public Users isVerifiedUserCredentials(String email, String password) {
        return manager.isVerifiedUserCredentials(email, password);
    }

    @Override
    public void registerNewUser(Users u) {
        manager.registerNewUser(u);
    }

    @Override
    public List<Users> getAllUsers() {
        return manager.getAllUsers();
    }

    @Override
    public List<Cpu> getUserCpus(Users u) {
        return manager.getUserCpus(u);
    }

    @Override
    public List<Experiments> getUserExperiments(Users u) {
        return  manager.getUserExperiments(u);
    }

    @Override
    public void addNewExperiment(Experiments exp) {
        manager.addNewExperiment(exp);
    }

}
