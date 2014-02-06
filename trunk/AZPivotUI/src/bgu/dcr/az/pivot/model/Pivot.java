/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import bgu.dcr.az.orm.api.Data;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;

/**
 *
 * @author User
 */
public interface Pivot {

    /**
     * <b>Series</b> represented as <b>Legend</b> at the <i>graph view</i> and
     * as
     * <b>Columns</b> labels at the <i>table view</i>. The each value of Series
     * calculated by enumerating all possible combinations of the selected
     * fields data values.
     *
     * @return the collection of selected Series fields
     */
    ObservableList<Field> getSelectedSeriesFields();

    /**
     * <b>Values</b> represented as <b>internal data</b> at the <i>table
     * view</i>
     * and as <b>height</b> of the data points at the <i>graph view</i>. The
     * values computed by the partitioning of the initial data into sets
     * according to {
     *
     * @see getSelectedSeriesFields()} and {
     * @see getSelectedAxisFields()} values. Then filtering is applied using
     * predefined values of {
     * @see getSelectedFilterFields()} fields. Finally the value calculated by
     * applying {
     * @see AggregationFunction} on the calculated sets.
     * @return the collection of selected Values aggregated fields
     */
    ObservableList<AggregatedField> getSelectedValuesFields();

    /**
     * <b>Axis</b> represented as <b>Categories</b> at the <i>graph view</i> and
     * as
     * <b>Rows</b> labels at the <i>table view</i>. The each value of Axis
     * calculated by enumerating all possible combinations of the selected
     * fields data values.
     *
     * @return the collection of selected Axis fields
     */
    ObservableList<Field> getSelectedAxisFields();

    /**
     * <b>Filter</b> fields allows to filter {
     *
     * @see getSelectedValuesFields()} data according to the selected Fields
     * with restricted values.
     * @return the collection of Filter fields
     */
    ObservableList<FilterField> getSelectedFilterFields();

    List<Field> getAvailableRawFields();

    Data getData();

    TableData getPivotedData();

}
