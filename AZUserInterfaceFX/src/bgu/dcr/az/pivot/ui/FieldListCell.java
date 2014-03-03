/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.ui;

import bgu.dcr.az.pivot.model.AggregatedField;
import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.Pivot;
import bgu.dcr.az.pivot.model.impl.FieldUtils;
import bgu.dcr.az.pivot.model.impl.PivotUtils;
import bgu.dcr.az.ui.screens.dialogs.Notification;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author bennyl
 */
public class FieldListCell extends ListCell<Field> {

    CheckBox check = null;
    Label lbl = null;
    BorderPane border;
    boolean showCheckBox;
    Pivot pivot;
    TextField text;
    PivotViewController.ListCellFactory factory;
    ChoiceBox choice;

    public FieldListCell(boolean showCheckBox, boolean editable, Pivot pivot, PivotViewController.ListCellFactory factory) {
        this.factory = factory;
        this.showCheckBox = showCheckBox;
        this.pivot = pivot;
        setEditable(editable);
    }

    public void update() {
        updateItem(getItem(), false);
    }

    public PivotViewController.ListCellFactory getFactory() {
        return factory;
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (getItem() == null) {
            return;
        }

        if (text == null) {
            text = new TextField();

            text.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    try {
                        getItem().setFieldName(text.getText());
                        commitEdit(getItem());
                    } catch (Throwable ex) {
                        cancelEdit();
                        update();
                        Notification.exception(ex);
                    }
                }
            });
        }

        text.setText(getItem().getFieldName());
        setGraphic(text);
        text.requestFocus();
    }

    @Override
    public void commitEdit(Field t) {
        super.commitEdit(t);
        factory.refreshCells();
    }

    @Override
    protected void updateItem(Field t, boolean empty) {
        Field oldItem = getItem();
        if (oldItem != null && oldItem instanceof AggregatedField) {
            choice.valueProperty().unbindBidirectional(((AggregatedField) oldItem).aggregationFunctionProperty());
        }
        super.updateItem(t, empty);

        if (t == null || empty) {
            setGraphic(null);
        } else if (showCheckBox) {
            showCheckbox(t);
        } else if (t instanceof AggregatedField) {
            showChoicebox((AggregatedField) t);
        } else {
            if (lbl == null) {
                lbl = new Label();
                lbl.setPadding(new Insets(3));
            }

            lbl.setText(t.toString());
            setGraphic(lbl);
        }
    }

    private void showCheckbox(Field t) {
        if (check == null) {
            check = new CheckBox();
            lbl = new Label();
            border = new BorderPane();
            BorderPane.setAlignment(lbl, Pos.CENTER_LEFT);
            border.setCenter(lbl);
            border.setLeft(check);
        }

        check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> v, Boolean ov, Boolean nv) {
                if (getItem() == null) {
                    return;
                }
                if (!nv) {
                    PivotUtils.removeUseOf(pivot, getItem().getField());
                } else {
                    check.selectedProperty().setValue(PivotUtils.isInUse(pivot, getItem().getField()));
                }
            }
        });

        lbl.setText(t.toString());
        check.selectedProperty().setValue(PivotUtils.isInUse(pivot, t));
        setGraphic(border);
    }

    private void showChoicebox(AggregatedField t) {
        if (choice == null) {
            choice = new ChoiceBox();
            choice.getItems().addAll(FieldUtils.getDefaultAggregationFunctions());
            choice.setMinWidth(23);
            choice.setMaxWidth(23);
            choice.setMinHeight(23);
            choice.setMaxHeight(23);

            lbl = new Label();
            lbl.setPadding(new Insets(0, 0, 0, 8));
            BorderPane.setAlignment(lbl, Pos.CENTER_LEFT);

            border = new BorderPane();
            border.setLeft(choice);
            border.setCenter(lbl);
        }

        lbl.setText(t.getFieldName());
        choice.valueProperty().unbind();
        choice.valueProperty().set(t.aggregationFunctionProperty().get());
        choice.valueProperty().bindBidirectional(t.aggregationFunctionProperty());
        setGraphic(border);
    }
}
