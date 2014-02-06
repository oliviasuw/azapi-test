/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.utils.ImmutableSetView;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.RecordAccessor;
import bgu.dcr.az.pivot.model.AggregatedField;
import bgu.dcr.az.pivot.model.AggregationFunction;
import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.FilterField;
import bgu.dcr.az.pivot.model.Pivot;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 *
 * @author vadim
 */
public abstract class AbstractPivot implements Pivot {

    private final ObservableList<Field> selectedAxis = FXCollections.observableArrayList();
    private final ObservableList<FilterField> selectedFilters = FXCollections.observableArrayList();
    private final ObservableList<Field> selectedSeries = FXCollections.observableArrayList();
    private final ObservableList<AggregatedField> selectedValues = FXCollections.observableArrayList();
    private List<Field> availableRawFields;
    private final Data data;

    public AbstractPivot(Data data) {
        this.data = data;

        initializeAvailableRawFields();

        selectedAxis.addListener(new ListChangeListener<Field>() {
            @Override
            public void onChanged(Change<? extends Field> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (Field f : c.getAddedSubList()) {
                            try {
                                validateAxisField(f);
                            } catch (UnavailableFieldException ex) {
                                selectedAxis.remove(f);
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });

        selectedFilters.addListener(new ListChangeListener<FilterField>() {
            @Override
            public void onChanged(Change<? extends FilterField> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (FilterField f : c.getAddedSubList()) {
                            try {
                                validateFilterField(f.getField());
                            } catch (UnavailableFieldException ex) {
                                selectedFilters.remove(f);
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });

        selectedSeries.addListener(new ListChangeListener<Field>() {
            @Override
            public void onChanged(Change<? extends Field> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (Field f : c.getAddedSubList()) {
                            try {
                                validateSeriesField(f);
                            } catch (UnavailableFieldException ex) {
                                selectedSeries.remove(f);
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });

        selectedValues.addListener(new ListChangeListener<AggregatedField>() {
            @Override
            public void onChanged(Change<? extends AggregatedField> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (AggregatedField f : c.getAddedSubList()) {
                            try {
                                validateValuesField(f);
                            } catch (UnavailableFieldException ex) {
                                selectedValues.remove(f);
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public ObservableList<Field> getSelectedSeriesFields() {
        return selectedSeries;
    }

    @Override
    public ObservableList<AggregatedField> getSelectedValuesFields() {
        return selectedValues;
    }

    @Override
    public ObservableList<Field> getSelectedAxisFields() {
        return selectedAxis;
    }

    @Override
    public ObservableList<FilterField> getSelectedFilterFields() {
        return selectedFilters;
    }

    @Override
    public Data getData() {
        return data;
    }

    public void validateFieldName(Field field, String newName) {
        if (newName.isEmpty()) {
            throw new RuntimeException("Each field must have a name.");
        }

        for (Field f : getAvailableRawFields()) {
            if (f.getFieldId() != field.getFieldId() && f.getFieldName().equals(newName)) {
                throw new RuntimeException("Field with same name already exists.");
            }
        }
    }

    public void validateSeriesField(Field field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        if (selectedAxis.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an axis field.");
        }

        if (PivotUtils.isInValueFields(this, field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an value field.");
        }
    }

    public void validateValuesField(final AggregatedField field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field.getField())) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        if (!Number.class.isAssignableFrom(field.getMetadata().type())) {
            throw new UnavailableFieldException("The value field must have a numeric type.");
        }

        if (selectedAxis.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an axis field.");
        }

        if (selectedSeries.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an series field.");
        }
    }

    public void validateAxisField(Field field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }

        if (selectedSeries.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an series field.");
        }

        if (PivotUtils.isInValueFields(this, field)) {
            throw new UnavailableFieldException("The field: " + field + " is already selected as an value field.");
        }
    }

    public void validateFilterField(Field field) throws UnavailableFieldException {
        if (!availableRawFields.contains(field)) {
            throw new UnavailableFieldException("The field: " + field + " does not exest at pivot's available fields list.");
        }
    }

    @Override
    public List<Field> getAvailableRawFields() {
        return availableRawFields;
    }

    private void initializeAvailableRawFields() {
        availableRawFields = new LinkedList<>();

        for (int i = 0; i < data.columns().length; i++) {
            FieldMetadata md = data.columns()[i];
            final SimpleField field = new SimpleField(this, i, md.name(), md);
            availableRawFields.add(field);
        }
    }

    public static class SimpleFilterField<T> extends FieldWrapper<T> implements FilterField<T> {

        private final ObservableSet<T> restricted = FXCollections.observableSet();
        private final HashSet<T> all;

        public SimpleFilterField(Pivot pivot, Field<T> field) {
            super(field);
            all = new HashSet<>();
            for (RecordAccessor r : pivot.getData()) {
                all.add(field.getValue(r));
            }

            restricted.addListener(new SetChangeListener<T>() {
                @Override
                public void onChanged(SetChangeListener.Change<? extends T> c) {
                    if (c.wasAdded()) {
                        try {
                            restrictValue(c.getElementAdded());
                        } catch (UnavailableValueException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    if (c.wasRemoved()) {
                        try {
                            allowValue(c.getElementRemoved());
                        } catch (UnavailableValueException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });
        }

        @Override
        public ImmutableSetView<T> getAllValues() {
            return new ImmutableSetView(all);
        }

        @Override
        public ObservableSet<T> getRestrictedValues() {
            return restricted;
        }

        private void restrictValue(T value) throws UnavailableValueException {
            if (!all.contains(value)) {
                throw new UnavailableValueException("The value: " + value + " is not a legal value of field: " + getFieldName());
            }
        }

        private void allowValue(T value) throws UnavailableValueException {
            if (!all.contains(value)) {
                throw new UnavailableValueException("The value: " + value + " is not a legal value of field: " + getFieldName());
            }
        }

        @Override
        public String toString() {
            return getFieldName();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SimpleFilterField && ((SimpleFilterField)obj).getFieldId() == getFieldId();
        }
    }

    public static class SimpleAggregatedField<T> extends FieldWrapper<T> implements AggregatedField<T> {

        private final int aggregatedFieldId;
        private final ObjectProperty<AggregationFunction<T>> aggregationFunction = new SimpleObjectProperty<>();
        private String fieldName;
        private final AbstractPivot pivot;

        public SimpleAggregatedField(AbstractPivot pivot, Field<T> field, int aggregatedId) {
            super(field);
            this.pivot = pivot;
            aggregatedFieldId = aggregatedId;
            aggregationFunction.set(FieldUtils.getDefaultAggregationFunctions().iterator().next());
            updateAggregatedName(aggregationFunction.get());

            aggregationFunction.addListener(new ChangeListener<AggregationFunction<T>>() {
                @Override
                public void changed(ObservableValue<? extends AggregationFunction<T>> ov, AggregationFunction<T> pv, AggregationFunction<T> nv) {
                    updateAggregatedName(nv);
                }
            });
        }

        private void updateAggregatedName(AggregationFunction nv) {
            String init = "" + aggregationFunction.get().getName() + " of " + getField().getFieldName();
            try {
                pivot.validateFieldName(this, init);
                setFieldName(init);
            } catch (Exception e) {
                for (int i = 0; i < pivot.getAvailableRawFields().size() + 1; i++) {
                    try {
                        pivot.validateFieldName(this, init + " #" + i);
                        setFieldName(init + " #" + i);
                        break;
                    } catch (Exception ex) {}
                }
            }
        }

        @Override
        public int getAggregatedFieldId() {
            return aggregatedFieldId;
        }

        @Override
        public ObjectProperty<AggregationFunction<T>> aggregationFunctionProperty() {
            return aggregationFunction;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public final void setFieldName(String name) {
            pivot.validateFieldName(this, name);
            fieldName = name;
        }

        @Override
        public String toString() {
            return getFieldName();
        }
    }
}
