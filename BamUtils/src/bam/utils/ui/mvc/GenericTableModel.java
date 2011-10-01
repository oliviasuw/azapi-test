/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bennyl
 */
public class GenericTableModel<T> extends AbstractTableModel {

    List<T> model;
    DataExtractor<T> extractor;
    
    public GenericTableModel(DataExtractor<T> extractor) {
        model = new LinkedList<T>();
        this.extractor = extractor;
    }
    
    @Override
    public int getRowCount() {
        return model.size();
    }

    @Override
    public int getColumnCount() {
        return extractor.getSupportedDataNames().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return extractor.getData(extractor.getSupportedDataNames()[columnIndex], model.get(rowIndex));
    }

    public void fillWith(List<T> ts) {
        model.addAll(ts);
        fireTableRowsInserted(model.size()-ts.size(), model.size());
    }

    @Override
    public String getColumnName(int column) {
        return extractor.getSupportedDataNames()[column];
    }

    public List<T> getInnerData() {
        return model;
    }

    public T itemAt(int idx) {
        return model.get(idx);
    }

    public int findRowWith(T s) {
        return model.indexOf(s);
    }
    
}
