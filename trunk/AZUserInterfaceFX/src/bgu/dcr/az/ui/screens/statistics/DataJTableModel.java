/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.orm.api.Data;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author User
 */
public class DataJTableModel extends AbstractTableModel {

    private Data data;

    public DataJTableModel(Data data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.numRecords();
    }

    @Override
    public int getColumnCount() {
        return data.columns().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.getRecord(rowIndex).get(columnIndex);
    }
}
