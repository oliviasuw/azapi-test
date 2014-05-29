/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.registery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Shl
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Register {
    String value();
}
