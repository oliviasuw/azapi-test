/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

/**
 *
 * @author User
 */
public interface Field<T, F> {

    Pivot<F> getParent();
    
    String getFieldName();

    void setFieldName(String name);

    Class<T> getFieldType();

    int getFieldId();

    T getValue(F o);
}
