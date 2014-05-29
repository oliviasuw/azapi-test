/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.FieldMetadata;
import bgu.dcr.az.execs.orm.api.RecordAccessor;
import bgu.dcr.az.execs.orm.FieldMetadataImpl;
import bgu.dcr.az.execs.orm.SimpleRecordAccessor;
import bgu.dcr.az.pivot.model.TableData;
import java.util.Iterator;

/**
 *
 * @author User
 */
public class PlottableTableData implements Data {
    private TableData data;
    FieldMetadata[] columns;

    public PlottableTableData(TableData data) {
        this.data = data;
        columns = new FieldMetadata[3];
        
        columns[0] = data.columns()[0];
        columns[1] = new FieldMetadataImpl("Series", String.class);
        columns[2] = new FieldMetadataImpl("Values", Double.class);
    }
    
    @Override
    public int numRecords() {
        return data.numRecords() * (data.columns().length - 1);
    }

    @Override
    public FieldMetadata[] columns() {
        return columns;
    }

    @Override
    public RecordAccessor getRecord(int i) {
        return new SimpleRecordAccessor(columns, new PlotablePivotRecord(data, i));
    }

    @Override
    public Iterator<RecordAccessor> iterator() {
        return new Iterator<RecordAccessor>() {
            private int i = 0;
            
            @Override
            public boolean hasNext() {
                return i < numRecords();
            }

            @Override
            public RecordAccessor next() {
                return getRecord(i++);
            }
        };
    }
    
    
    
}
