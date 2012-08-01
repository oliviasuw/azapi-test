/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Experiment;
import bgu.dcr.az.db.ent.User;
import bgu.dcr.az.db.ent.UserRole;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Inka
 */
@ManagedBean(eager = true, name = "dbManager")
@ApplicationScoped
public class DB extends DBManager {

    public DB() {
        super();
        try {
            User u = new User("bla@bla.bla", "inka", "123", "!!!", UserRole.DCR);
            save(u);
            final Experiment e1 = new Experiment("Inna", "no Description", u, "");
            e1.setPublicExp(true);
            save(e1);
            final Experiment e2 = new Experiment("Dima", "bla bla bla", u, "");
            e2.setPublicExp(true);
            save(e2);
            final Experiment e3 = new Experiment("Benny", "!!!!!!!!!!!!!!!!", u, "");
            e3.setPublicExp(true);
            save(e3);
            final Experiment e4 = new Experiment("Inka", "the description is", u, "");
            e4.setPublicExp(true);
            save(e4);
        } catch (Exception ex) {
        }
    }
}
