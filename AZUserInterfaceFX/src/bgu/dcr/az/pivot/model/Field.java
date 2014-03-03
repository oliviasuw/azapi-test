/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.RecordAccessor;
import javafx.beans.property.StringProperty;

/**
 *
 * @author User
 * @param <T>
 */
public interface Field<T> {

    String getFieldName();
    
    void setFieldName(String name);

    FieldMetadata getMetadata();

    int getFieldId();

    T getValue(RecordAccessor r);
    
    Field getField();
}
