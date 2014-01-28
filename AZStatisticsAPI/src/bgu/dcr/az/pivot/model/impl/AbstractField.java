/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.Pivot;

/**
 *
 * @author User
 */
public abstract class AbstractField<T, F> implements Field<T, F> {

    private final Pivot<F> pivot;
    private String name;
    private final int id;
    private final Class<T> type;

    public AbstractField(Pivot<F> pivot, String name, int id, Class<T> type) {
        this.pivot = pivot;
        this.name = name;
        this.id = id;
        this.type = type;
    }

    @Override
    public Pivot<F> getParent() {
        return pivot;
    }
    
    @Override
    public String getFieldName() {
        return name;
    }

    @Override
    public void setFieldName(String name) {
        this.pivot.beforeFieldNameChanged(this, name);
        String oldName = this.name;
        this.name = name; 
        this.pivot.afterFieldNameChanged(this, oldName);
    }

    @Override
    public Class<T> getFieldType() {
        return type;
    }

    @Override
    public int getFieldId() {
        return id;
    }

    @Override
    public String toString() {
        return getFieldName();
    }
        
}
