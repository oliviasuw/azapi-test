/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.ui;

import bgu.dcr.az.orm.api.RecordAccessor;
import bgu.dcr.az.pivot.model.TableData;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Zovadi
 */
public class PivotDataTableViewController implements Initializable {

    @FXML
    private TableView table;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setModel(TableData data) {
        table.getColumns().clear();
        table.getColumns().addAll(generateColumns(data));
        ObservableList<Object> list = FXCollections.observableArrayList();
        data.spliterator().forEachRemaining(f -> list.add(f));
        table.setItems(list);
    }

    private List generateColumns(TableData data) {
        if (data.getColumnHeaders().numberOfHeaders() == 0) {
            return Collections.EMPTY_LIST;
        }

        LinkedList result = new LinkedList();
        final TableColumn rowLabelsColumn = new TableColumn("Row labels");
        rowLabelsColumn.setCellValueFactory(new PropertyValueFactory<RecordAccessor, Object>("") {
            @Override
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<RecordAccessor, Object> cdf) {
                return new SimpleObjectProperty<>(cdf.getValue().get(0));
            }
        });
        result.add(rowLabelsColumn);

        TableColumn[] columns = new TableColumn[data.getColumnHeaders().getHeader(0).length];
        int id = 0;
        for (Object[] ch : data.getColumnHeaders()) {
            for (int i = 0; i < ch.length; i++) {
                if (columns[i] == null || !columns[i].getText().equals(ch[i].toString())) {
                    columns[i] = new TableColumn(ch[i].toString());
                    if (i != 0) {
                        columns[i - 1].getColumns().add(columns[i]);
                    } else {
                        result.add(columns[0]);
                    }
                    if (i == ch.length - 1) {
                        final int fid = id;
                        columns[i].setCellValueFactory(new PropertyValueFactory<RecordAccessor, Object>("") {
                            @Override
                            public ObservableValue<Object> call(TableColumn.CellDataFeatures<RecordAccessor, Object> cdf) {
                                return new SimpleObjectProperty<>(cdf.getValue().get(fid + 1));
                            }
                        });
                    }
                    for (int j = i + 1; j < ch.length; j++) {
                        columns[j] = null;
                    }
                }
            }
            id++;
        }

        return result;
    }
}
