/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.scan;

/**
 *
 * @author bennyl
 */
public interface Box<T> {
    T get();
    boolean set(T val);
    boolean isReadOnly();
    Class getType();
}
