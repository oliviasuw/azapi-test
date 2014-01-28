/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.utils.ImmutableSetView;
import bgu.dcr.az.utils.ImmutableCollectionView;
import bgu.dcr.az.anop.utils.EventListeners;
import bgu.dcr.az.pivot.model.AggregatedField;
import bgu.dcr.az.pivot.model.AggregationFunction;
import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.FilterField;
import bgu.dcr.az.pivot.model.Pivot;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author vadim
 * @param <T>
 */
public abstract class AbstractPivot<T> implements Pivot<T> {

    private final LinkedList<Field<?, T>> selectedAxis;
    private final Set<FilterField<?, T>> selectedFilters;
    private final LinkedList<Field<?, T>> selectedSeries;
    private final LinkedList<AggregatedField<? extends Object, T>> selectedValues;
    private Set<Field<?, T>> availableRawFields;
    private final Collection<T> dataRecords;
    private Set<AggregationFunction> aggregationFunctions;
    private int aggregationIdGenerator = 0;
    private final EventListeners<PivotListener> listeners = EventListeners.create(PivotListener.class);

    public AbstractPivot(Collection<T> dataRecords) {
        this.selectedAxis = new LinkedList<>();
        this.selectedFilters = new HashSet<>();
        this.selectedSeries = new LinkedList<>();
        this.selectedValues = new LinkedList<>();
        this.dataRecords = dataRecords;
    }

    public void setAvailableRawFields(Set<Field<?, T>> availableRawFields) {
        this.availableRawFields = availableRawFields;
    }

    public void setAggregationFunctions(Set<AggregationFunction> aggregationFunctions) {
        this.aggregationFunctions = aggregationFunctions;
    }

    @Override
    public void beforeFieldNameChanged(Field field, String newName) {
        if (newName.isEmpty()) {
            throw new RuntimeException("Each field must have a name.");
        }

        for (Field f : getAvailableRawFields()) {
            if (f.getFieldId() != field.getFieldId() && f.getFieldName().equals(newName)) {
                throw new RuntimeException("Field with same name already exists.");
            }
        }
    }

    @Override
    public void afterFieldNameChanged(Field field, String oldName) {
        listeners.fire().pivotChanged(this);
    }

    @Override
    public ImmutableCollectionView<Field> getSelectedSeriesFields() {
        return new ImmutableCollectionView(selectedSeries);
    }

    @Override
    public ImmutableCollectionView<AggregatedField> getSelectedValuesFields() {
        return new ImmutableCollectionView(selectedValues);
    }

    @Override
    public ImmutableCollectionView<Field> getSelectedAxisFields() {
        return new ImmutableCollectionView(selectedAxis);
    }

    @Override
    public ImmutableCollectionView<FilterField> getSelectedFilterFields() {
        return new ImmutableCollectionView(selectedFilters);
    }

    @Override
    public Field<?, T> selectSeriesField(Field<?, T> field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        if (selectedAxis.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an axis field.");
        }

        if (isInValueFields(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an value field.");
        }

        selectedSeries.add(field);

        listeners.fire().pivotChanged(this);

        return field;
    }

    @Override
    public AggregatedField<?, T> selectValuesField(final Field<?, T> field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        if (!Number.class.isAssignableFrom(field.getFieldType())) {
            throw new UnavailableFieldException("The value field must have a numeric type.");
        }

        if (selectedAxis.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an axis field.");
        }

        if (selectedSeries.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an series field.");
        }

        AggregatedField aggregatedField = new SimpleAggregatedField(this, field);

        selectedValues.add(aggregatedField);

        listeners.fire().pivotChanged(this);

        return aggregatedField;
    }

    @Override
    public Field<?, T> selectAxisField(Field<?, T> field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        if (selectedSeries.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an series field.");
        }

        if (isInValueFields(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an value field.");
        }

        selectedAxis.add(field);

        listeners.fire().pivotChanged(this);

        return field;
    }

    @Override
    public FilterField<?, T> selectFilterField(final Field<?, T> field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        FilterField filterField = new SimpleFilterField(this, field);

        selectedFilters.add(filterField);

        listeners.fire().pivotChanged(this);

        return filterField;
    }

    @Override
    public Set<Field> getAvailableRawFields() {
        return (Set) availableRawFields;
    }

    @Override
    public ImmutableCollectionView<T> getDataRecords() {
        return new ImmutableCollectionView(dataRecords);
    }

    @Override
    public Set<AggregationFunction> getAggregationFunctions() {
        return aggregationFunctions;
    }

    @Override
    public boolean isInUse(Field element) {
        if (this.selectedAxis.contains(element) || this.selectedSeries.contains(element)) {
            return true;
        }

        return isInValueFields(element);
    }

    private boolean isInValueFields(Field element) {
        for (AggregatedField<?, T> e : this.selectedValues) {
            if (e.getFieldId() == element.getFieldId()) {
                return true;
            }
        }
        return false;
    }

    private boolean removeUseOfNonValueField(Field<?, T> field) {
        Iterator<FilterField<?, T>> it = selectedFilters.iterator();

        while (it.hasNext()) {
            if (it.next().getFieldId() == field.getFieldId()) {
                it.remove();
                return true;
            }
        }

        return this.selectedAxis.remove(field) || this.selectedSeries.remove(field);
    }

    @Override
    public void removeUseOf(Field element) {

        if (element instanceof AggregatedField) {
            AggregatedField field = (AggregatedField) element;

            for (Iterator<AggregatedField<?, T>> it = this.selectedValues.iterator(); it.hasNext();) {
                AggregatedField<?, T> e = it.next();
                if (e.getAggregatedFieldId() == field.getAggregatedFieldId()) {
                    it.remove();
                    listeners.fire().pivotChanged(this);
                    return;
                }
            }
            return;
        }

        if (removeUseOfNonValueField(element)) {
            listeners.fire().pivotChanged(this);
            return;
        }

        boolean removed = false;

        for (Iterator<AggregatedField<?, T>> it = this.selectedValues.iterator(); it.hasNext();) {
            AggregatedField<?, T> e = it.next();
            if (e.getFieldId() == element.getFieldId()) {
                it.remove();
                removed = true;
            }
        }

        if (removed) {
            listeners.fire().pivotChanged(this);
        }
    }

    @Override
    public EventListeners<PivotListener> getListeners() {
        return listeners;
    }

    private class SimpleFilterField implements FilterField {

        private final Set<Object> restricted;
        private final Pivot<T> pivot;
        private final Field<?, T> field;
        private final HashSet<Object> all;

        public SimpleFilterField(Pivot<T> pivot, Field<?, T> field) {
            this.pivot = pivot;
            this.field = field;
            all = new HashSet<>();
            restricted = new HashSet<>();
            for (T r : dataRecords) {
                all.add(field.getValue(r));
            }
        }

        @Override
        public ImmutableSetView<T> getAllValues() {
            return new ImmutableSetView(all);
        }

        @Override
        public ImmutableSetView getRestrictedValues() {
            return new ImmutableSetView<>(restricted);
        }

        @Override
        public void restrictValue(Object value) throws UnavailableValueException {
            if (!all.contains(value)) {
                throw new UnavailableValueException("The value: " + value + " is not a legal value of field: " + field.getFieldName());
            }

            restricted.add(value);

            listeners.fire().pivotChanged(pivot);
        }

        @Override
        public void allowValue(Object value) throws UnavailableValueException {
            if (!all.contains(value)) {
                throw new UnavailableValueException("The value: " + value + " is not a legal value of field: " + field.getFieldName());
            }

            if (restricted.remove(value)) {
                listeners.fire().pivotChanged(pivot);
            }
        }

        @Override
        public Pivot getParent() {
            return field.getParent();
        }

        @Override
        public String getFieldName() {
            return field.getFieldName();
        }

        @Override
        public void setFieldName(String name) {
            field.setFieldName(name);
        }

        @Override
        public Class getFieldType() {
            return field.getClass();
        }

        @Override
        public int getFieldId() {
            return field.getFieldId();
        }

        @Override
        public Object getValue(Object o) {
            return field.getValue((T) o);
        }

        @Override
        public String toString() {
            return getFieldName();
        }
    }

    private class SimpleAggregatedField implements AggregatedField {

        private final int aggregatedFieldId;
        private AggregationFunction<T> aggregationFunction;
        private final Pivot<T> pivot;
        private final Field<?, T> field;
        private String fieldName;

        public SimpleAggregatedField(Pivot<T> pivot, Field<?, T> field) {
            aggregatedFieldId = aggregationIdGenerator++;
            aggregationFunction = getAggregationFunctions().iterator().next();
            this.pivot = pivot;
            this.field = field;
            fieldName = "" + aggregationFunction.getName() + " of " + field.getFieldName();
        }

        @Override
        public int getAggregatedFieldId() {
            return aggregatedFieldId;
        }

        @Override
        public AggregationFunction getAggregationFunction() {
            return aggregationFunction;
        }

        @Override
        public void setAggregationFunction(AggregationFunction function) {
            if (fieldName.equals("" + aggregationFunction.getName() + " of " + field.getFieldName())) {
                fieldName = "" + function.getName() + " of " + field.getFieldName();
            }

            aggregationFunction = function;

            listeners.fire().pivotChanged(pivot);
        }

        @Override
        public Pivot getParent() {
            return pivot;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public void setFieldName(String name) {
            beforeFieldNameChanged(this, name);
            String oldName = fieldName;
            fieldName = name;
            afterFieldNameChanged(field, oldName);
        }

        @Override
        public Class getFieldType() {
            return field.getFieldType();
        }

        @Override
        public int getFieldId() {
            return field.getFieldId();
        }

        @Override
        public Object getValue(Object o) {
            return field.getValue((T) o);
        }

        @Override
        public String toString() {
            return getFieldName();
        }
    }
}
