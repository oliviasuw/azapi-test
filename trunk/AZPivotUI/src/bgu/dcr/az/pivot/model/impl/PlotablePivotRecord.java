/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.orm.api.RecordAccessor;
import bgu.dcr.az.orm.impl.Record;
import bgu.dcr.az.pivot.model.TableData;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author User
 */
public class PlotablePivotRecord implements Record {

    TableData data;
    Object series, value, category;

    public PlotablePivotRecord(TableData data, int i) {
        this.data = data;
        final int numRecords = data.numRecords();

        series = data.columns()[1 + i / numRecords].name();
        final RecordAccessor record = data.getRecord(i % numRecords);
        value = record.get(1 + i / numRecords);
        category = record.get(0);
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0:
                return series;
            case 1:
                return category;
            case 2:
                return value;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public int length() {
        return 3;
    }

    @Override
    public Iterator<Object> iterator() {
        return Arrays.asList(series, category, value).iterator();
    }

}
