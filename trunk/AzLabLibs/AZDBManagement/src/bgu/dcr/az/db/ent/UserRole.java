/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.db.ent;

import javax.persistence.Embeddable;

/**
 *
 * @author Inka
 */
@Embeddable
public enum UserRole {
    UNAUTHORIZED(false),
    DCR(true),
    APPROVED(true);

    boolean safety;

    private UserRole(boolean safety) {
        this.safety = safety;
    }
    
    public boolean getSafety() {
        return safety;
    }

}
