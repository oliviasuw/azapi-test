/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

/**
 *
 * @author User
 */
public interface AggregatedField<T, F> extends Field<T, F> {

    int getAggregatedFieldId();

    AggregationFunction<T> getAggregationFunction();

    void setAggregationFunction(AggregationFunction<T> function);
}
