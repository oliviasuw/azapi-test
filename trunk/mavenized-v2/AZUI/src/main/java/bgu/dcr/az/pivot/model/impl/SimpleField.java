/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.execs.orm.api.FieldMetadata;
import bgu.dcr.az.execs.orm.api.RecordAccessor;
import bgu.dcr.az.pivot.model.Field;

/**
 *
 * @author User
 */
public class SimpleField<T> implements Field<T> {

    private String name;
    private final int id;
    private final FieldMetadata metadata;
    private final AbstractPivot pivot;

    public SimpleField(AbstractPivot pivot, int id, String name, FieldMetadata metadata) {
        this.pivot = pivot;
        this.name = name;
        this.id = id;
        this.metadata = metadata;
    }

    @Override
    public int getFieldId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getFieldName() {
        return name;
    }

    @Override
    public final void setFieldName(String name) {
        pivot.validateFieldName(this, name);
        this.name = name;
    }
    
    @Override
    public FieldMetadata getMetadata() {
        return metadata;
    }

    @Override
    public T getValue(RecordAccessor r) {
        return (T) r.get(id);
    }

    @Override
    public Field getField() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Field && ((Field)obj).getFieldId() == getFieldId();
    }

}
