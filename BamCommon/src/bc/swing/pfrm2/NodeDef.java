/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author BLutati
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface NodeDef {

    String id();

    String[] roles() default {};

    Class[] view() default {};

    String[] att() default {};

    public static class Actions {
        public static void setAttributes(NodeDef ndef, Node n) {
            n.putAtt(Att.ROLES, ndef.roles());
            n.putAtt(Att.ID, ndef.id());
            if (ndef.view().length > 0){
                n.putAtt(Att.VIEW_CLASS, ndef.view()[0]);
            }
            
            for (int i=0; i<ndef.att().length; i+=2){
                n.putAtt(ndef.att()[i], ndef.att()[i+1]);
            }            
        }
    }
}
