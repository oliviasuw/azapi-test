/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistiscs.pivot;

import bgu.dcr.az.orm.api.RecordAccessor;
import bgu.dcr.az.pivot.model.TableData;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Zovadi
 */
public class PivotDataTableView extends TableView {

    public static int MAXIMUM_SHOWN_COLUMNS = 100;
    private static final boolean removeSingle = true;
    private final boolean showRowHeaders;

    public PivotDataTableView(boolean showRowHeaders) {
        this.showRowHeaders = showRowHeaders;
//        setStyle("-fx-border-color:red; -fx-border-width:2;");
    }

    public void setModel(TableData data) {
        if (data != null) {
//            table.setMaxHeight(Double.MAX_VALUE);
//            table.setMaxWidth(Double.MAX_VALUE);
//            table.minHeightProperty().bind(heightProperty().subtract(2));
//            table.minWidthProperty().bind(widthProperty().subtract(2));

            getColumns().addAll(generateColumns(data));
            ObservableList<Object> list = FXCollections.observableArrayList();
            data.spliterator().forEachRemaining(f -> list.add(f));
            setItems(list);
            setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//            autosize();
        }
    }

    private List generateColumns(TableData data) {
        if (data.numRecords() == 0) {
            return Collections.EMPTY_LIST;
        }

        LinkedList result = new LinkedList();
        if (showRowHeaders) {
            final TableColumn rowLabelsColumn = new TableColumn("Row labels");
            rowLabelsColumn.setCellValueFactory(new PropertyValueFactory<RecordAccessor, Object>("") {
                @Override
                public ObservableValue<Object> call(TableColumn.CellDataFeatures<RecordAccessor, Object> cdf) {
                    return new SimpleObjectProperty<>(cdf.getValue().get(0));
                }
            });

            rowLabelsColumn.setPrefWidth(100);
            result.add(rowLabelsColumn);
        }
        TableColumn[] columns = new TableColumn[data.getColumnHeaders().getHeader(0).length];
        int id = 0;
        int addedColumns = 0;
        TABLE_CREATION:
        for (Object[] ch : data.getColumnHeaders()) {
            for (int i = 0; i < ch.length; i++) {
                if (columns[i] == null || !columns[i].getText().equals(ch[i].toString())) {
                    if (i == ch.length - 1) {
                        if (addedColumns >= MAXIMUM_SHOWN_COLUMNS) {
                            break TABLE_CREATION;
                        }
                        addedColumns++;
                    }
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
                                if (showRowHeaders) {
                                    return new SimpleObjectProperty<>(cdf.getValue().get(fid + 1));
                                } else {
                                    return new SimpleObjectProperty<>(cdf.getValue().get(fid));
                                }
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

        if (removeSingle) {
            List<TableColumn> open = new LinkedList<>();
            open.addAll(result);

            while (!open.isEmpty()) {
                TableColumn parent = open.remove(0);
                if (parent.getColumns().size() == 1) {
                    TableColumn child = (TableColumn) parent.getColumns().get(0);
                    parent.getColumns().clear();
                    parent.getColumns().addAll(child.getColumns());
                    parent.setText(parent.getText() + "\n" + child.getText());
                    parent.setCellValueFactory(child.getCellValueFactory());
                }
                open.addAll(parent.getColumns());
            }
        }

        return result;
    }
}
