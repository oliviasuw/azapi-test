/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import bgu.dcr.az.pivot.model.impl.UnavailableValueException;
import bgu.dcr.az.utils.ImmutableSetView;

/**
 *
 * @author User
 */
public interface FilterField<T, F> extends Field<T, F> {

    ImmutableSetView<T> getRestrictedValues();
    
    ImmutableSetView<T> getAllValues();

    void restrictValue(T value) throws UnavailableValueException;
    
    void allowValue(T value) throws UnavailableValueException;
}
