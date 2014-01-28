/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import bgu.dcr.az.anop.utils.EventListeners;
import bgu.dcr.az.pivot.model.impl.UnavailableFieldException;
import bgu.dcr.az.utils.ImmutableCollectionView;
import java.util.Set;

/**
 *
 * @author User
 * @param <T>
 */
public interface Pivot<T> {
    
    void beforeFieldNameChanged(Field field, String newName);
    
    void afterFieldNameChanged(Field field, String oldName);
    
    /**
     * <b>Series</b> represented as <b>Legend</b> at the <i>graph view</i> and as 
     * <b>Columns</b> labels at the <i>table view</i>. The each value of Series 
     * calculated by enumerating all possible combinations of the selected 
     * fields data values.
     * @return the collection of selected Series fields
     */
    ImmutableCollectionView<Field> getSelectedSeriesFields();    

    /**
     * <b>Values</b> represented as <b>internal data</b> at the <i>table view</i>
     * and as <b>height</b> of the data points at the <i>graph view</i>. The
     * values computed by the partitioning of the initial data into sets according
     * to {@see getSelectedSeriesFields()} and {@see getSelectedAxisFields()} values. 
     * Then filtering is applied using predefined values of {@see getSelectedFilterFields()}
     * fields. Finally the value calculated by applying {@see AggregationFunction}
     * on the calculated sets.
     * @return the collection of selected Values aggregated fields
     */
    ImmutableCollectionView<AggregatedField> getSelectedValuesFields();

    /**
     * <b>Axis</b> represented as <b>Categories</b> at the <i>graph view</i> and as 
     * <b>Rows</b> labels at the <i>table view</i>. The each value of Axis 
     * calculated by enumerating all possible combinations of the selected 
     * fields data values.
     * @return the collection of selected Axis fields
     */
    ImmutableCollectionView<Field> getSelectedAxisFields();

    /**
     * <b>Filter</b> fields allows to filter {@see getSelectedValuesFields()} data 
     * according to the selected Fields with restricted values.
     * @return the collection of Filter fields
     */
    ImmutableCollectionView<FilterField> getSelectedFilterFields();
    
    Field selectSeriesField(Field<?, T> field) throws UnavailableFieldException;    
    
    AggregatedField selectValuesField(Field<?, T> field) throws UnavailableFieldException;    
    
    Field selectAxisField(Field<?, T> field) throws UnavailableFieldException;    
    
    FilterField selectFilterField(Field<?, T> field) throws UnavailableFieldException;    

    Set<Field> getAvailableRawFields();

    ImmutableCollectionView<T> getDataRecords();
    
    Set<AggregationFunction> getAggregationFunctions();
    
    Table getPivotTable();

    boolean isInUse(Field element);

    void removeUseOf(Field element);
    
    EventListeners<PivotListener> getListeners();
    
    public static interface PivotListener {
        
        void pivotChanged(Pivot pivot);
    }
}
