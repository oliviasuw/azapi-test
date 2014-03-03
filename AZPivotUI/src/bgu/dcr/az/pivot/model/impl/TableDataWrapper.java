/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.RecordAccessor;
import bgu.dcr.az.pivot.model.TableData;
import java.util.Iterator;

/**
 *
 * @author User
 */
public class TableDataWrapper implements TableData {

    private final Headers columnsHeaders;
    private final Headers rowsHeaders;
    private final Data data;

    public TableDataWrapper(Data data) {
        this.data = data;
        Object[][] columns = new Object[data.columns().length][];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Object[]{data.columns()[i].name()};
        }
        Object[][] rows = new Object[data.numRecords()][];
        for (int i = 0; i < rows.length; i++) {
            columns[i] = new Object[]{i};
        }
        columnsHeaders = new SimpleHeaders(columns);
        rowsHeaders = new SimpleHeaders(rows);
    }

    @Override
    public Headers getColumnHeaders() {
        return columnsHeaders;
    }

    @Override
    public Headers getRowHeaders() {
        return rowsHeaders;
    }

    @Override
    public int numRecords() {
        return data.numRecords();
    }

    @Override
    public FieldMetadata[] columns() {
        return data.columns();
    }

    @Override
    public RecordAccessor getRecord(int i) {
        return data.getRecord(i);
    }

    @Override
    public Iterator<RecordAccessor> iterator() {
        return data.iterator();
    }
}
