/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.Table;
import java.util.Collection;

/**
 *
 * @author User
 * @param <T>
 */
public class SimplePivot<T> extends AbstractPivot<T> {

    public SimplePivot(Collection<T> dataRecords) {
        super(dataRecords);
    }
    
    @Override
    public Table getPivotTable() {
        return new InMemoryPivotTable(this);
    }
}