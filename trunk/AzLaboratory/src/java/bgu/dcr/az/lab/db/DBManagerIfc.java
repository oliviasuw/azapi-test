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
public interface DBManagerIfc {

    void addNewExperiment(Experiments exp);

    int countAllArticles();

    List<Users> getAllUsers();

    List<Articles> getLastArticles(int pageNumber, int pageSize);

    List<Cpu> getUserCpus(Users u);

    List<Experiments> getUserExperiments(Users u);

    Users isVerifiedUserCredentials(String email, String password);

    void registerNewUser(Users u);
    
}
