/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

/**
 *
 * @author User
 */
public interface AggregationFunction<T> {

    String getName();

    Object aggregate(Iterable<T> values);
}
