/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.rnd;

import javax.swing.ImageIcon;

/**
 *
 * @author bennyl
 */
public interface IconSupplier<T> {
    ImageIcon supply(T item);
}
