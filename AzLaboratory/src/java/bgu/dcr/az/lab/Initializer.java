/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import bgu.dcr.az.lab.db.DBManager;
import java.sql.SQLException;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author kdima85
 */

@ManagedBean(eager = true)
@ApplicationScoped
public class Initializer {

    public Initializer() throws ClassNotFoundException, SQLException {
        initDB();
    }

    private void initDB() throws ClassNotFoundException, SQLException {
        DBManager.UNIT.init();

    }

    
    
    
}
