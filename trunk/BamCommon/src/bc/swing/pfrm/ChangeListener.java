/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

/**
 *
 * @author bennyl
 */
public interface ChangeListener {
    void onChangeHappened(Parameter source, Object newValue, DeltaHint deltaHint);
}
