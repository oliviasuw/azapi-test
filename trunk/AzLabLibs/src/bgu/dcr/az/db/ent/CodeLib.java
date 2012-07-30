/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import java.io.File;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Inka
 */
@Entity
public class CodeLib {
    private @Id @GeneratedValue long id = 0;
    private File locationOnDisk;
    private String name;

    protected CodeLib() {
    }

    public CodeLib(File locationOnDisk, String name) {
        this.locationOnDisk = locationOnDisk;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public File getLocationOnDisk() {
        return locationOnDisk;
    }

    public String getName() {
        return name;
    }

    public void setLocationOnDisk(File locationOnDisk) {
        this.locationOnDisk = locationOnDisk;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
