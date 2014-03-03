/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.common.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author User
 */
public class ConstantObservableValue<T> implements ObservableValue<T> {

    final T value;

    public ConstantObservableValue(T value) {
        this.value = value;
    }
    
    @Override
    public void addListener(ChangeListener<? super T> cl) {
        //there cannot be changes so no listener is needed
    }

    @Override
    public void removeListener(ChangeListener<? super T> cl) {
        //there cannot be changes so no listener is needed
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener il) {
        //there cannot be changes so no listener is needed
    }

    @Override
    public void removeListener(InvalidationListener il) {
        //there cannot be changes so no listener is needed
    }
    
}
