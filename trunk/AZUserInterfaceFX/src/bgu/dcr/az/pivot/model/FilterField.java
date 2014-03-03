/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import bgu.dcr.az.common.collections.immut.ImmutableSetView;
import javafx.collections.ObservableSet;

/**
 *
 * @author User
 * @param <T>
 */
public interface FilterField<T> extends Field<T> {

    ObservableSet<T> getRestrictedValues();

    ImmutableSetView<T> getAllValues();
}
