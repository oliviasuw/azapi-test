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
        return new InMemoryPivotTable(this);
    }

}
