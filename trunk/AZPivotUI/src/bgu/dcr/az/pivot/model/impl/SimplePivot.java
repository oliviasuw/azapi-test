/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.pivot.model.TableData;

/**
 *
 * @author User
 */
public class SimplePivot extends AbstractPivot {

    public SimplePivot(Data data) {
        super(data);
    }

    @Override
    public TableData getPivotedData() {
        System.out.println("Start calculating pivot");
        long start = System.currentTimeMillis();
        final InMemoryPivotTable pd = new InMemoryPivotTable(this);
        System.out.println("Pivot calculated in: " + (System.currentTimeMillis() - start));
        return pd;
    }

}
