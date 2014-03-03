/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author User
 * @param <T>
 */
public interface AggregatedField<T> extends Field<T> {

    int getAggregatedFieldId();

    ObjectProperty<AggregationFunction<T>> aggregationFunctionProperty();    
}
