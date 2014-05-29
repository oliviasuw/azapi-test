/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.execs.orm.api.FieldMetadata;
import bgu.dcr.az.execs.orm.api.RecordAccessor;
import bgu.dcr.az.pivot.model.Field;
import javafx.beans.property.StringProperty;

/**
 *
 * @author bennyl
 * @param <T>
 */
public class FieldWrapper<T> implements Field<T> {

    private final Field<T> field;

    public FieldWrapper(Field<T> field) {
        this.field = field;
    }
    
    @Override
    public Field getField() {
        return field;
    }

    @Override
    public String getFieldName() {
        return field.getFieldName();
    }

    @Override
    public void setFieldName(String name) {
        field.setFieldName(name);
    }

    @Override
    public FieldMetadata getMetadata() {
        return field.getMetadata();
    }

    @Override
    public int getFieldId() {
        return field.getFieldId();
    }

    @Override
    public T getValue(RecordAccessor r) {
        return field.getValue(r);
    }

    @Override
    public String toString() {
        return getFieldName();
    }
    
}
