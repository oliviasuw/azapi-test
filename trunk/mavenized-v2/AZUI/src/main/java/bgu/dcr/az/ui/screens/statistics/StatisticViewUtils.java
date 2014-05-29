/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.common.ui.ConstantObservableValue;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.RecordAccessor;
import java.util.Arrays;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author User
 */
public class StatisticViewUtils {

    public static TableView createTable(Data data) {
        TableView<RecordAccessor> tview = new TableView<>();

        tview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Arrays.stream(data.columns())
                .map(field -> new TableColumn<RecordAccessor, String>(field.name()))
                .forEach((TableColumn<RecordAccessor, String> column) -> {
                    tview.getColumns().add(column);
                    column.setCellValueFactory(arg -> new ConstantObservableValue<>(arg.getValue().getString(column.getText())));
                });

        data.forEach(tview.getItems()::add);
        return tview;
    }

    static void fillTable(TableView tview, Data data) {
        tview.getItems().clear();
        tview.getColumns().clear();
        
        tview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Arrays.stream(data.columns())
                .map(field -> new TableColumn<RecordAccessor, String>(field.name()))
                .forEach((TableColumn<RecordAccessor, String> column) -> {
                    tview.getColumns().add(column);
                    column.setCellValueFactory(arg -> new ConstantObservableValue<>(arg.getValue().getString(column.getText())));
                });

        data.forEach(tview.getItems()::add);
    }

}
