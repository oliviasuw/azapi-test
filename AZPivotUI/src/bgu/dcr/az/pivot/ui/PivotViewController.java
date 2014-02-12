/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.ui;

import bgu.dcr.az.pivot.ui.alert.AlertDialog;
import bgu.dcr.az.pivot.model.AggregatedField;
import bgu.dcr.az.pivot.model.Field;
import bgu.dcr.az.pivot.model.FilterField;
import bgu.dcr.az.pivot.model.impl.AbstractPivot;
import bgu.dcr.az.pivot.model.impl.PivotUtils;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author User
 */
public class PivotViewController implements Initializable {

    private int idRunner = 0;
    private Field draggedItem = null;

    private AbstractPivot pivot;
    private Stage stage;
    @FXML
    private ListView<Field> allFieldsListView;
    private ListCellFactory factory;
    @FXML
    private ListView<Field> columnsFieldsListView;
    @FXML
    private ListView<Field> rowsFieldsListView;
    @FXML
    private ListView<AggregatedField> valuesFieldsListView;
    @FXML
    private TreeView filterFieldsTreeView;
    private CheckBoxTreeItem treeRoot;

    public void setModel(AbstractPivot pivot) {
        this.pivot = pivot;
        factory = new ListCellFactory();

        initializeListView(allFieldsListView, FXCollections.observableArrayList(pivot.getAvailableRawFields()));
        initializeListView(columnsFieldsListView, pivot.getSelectedSeriesFields());
        initializeListView(rowsFieldsListView, pivot.getSelectedAxisFields());
        initializeListView(valuesFieldsListView, pivot.getSelectedValuesFields());
        initializeFilterFieldsModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void initializeFilterFieldsModel() {
        treeRoot = new CheckBoxTreeItem("Dummy");
        filterFieldsTreeView.setRoot(treeRoot);
        filterFieldsTreeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        pivot.getSelectedFilterFields().addListener(new ListChangeListener<FilterField>() {
            @Override
            public void onChanged(Change<? extends FilterField> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (FilterField ff : c.getRemoved()) {
                            treeRoot.getChildren().removeIf(e -> ((CheckBoxTreeItem) e).getValue().equals(ff));
                        }
                    }
                }
            }
        });

        filterFieldsTreeView.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Object item = filterFieldsTreeView.getSelectionModel().getSelectedItem();
                if (item != null && ((CheckBoxTreeItem) item).getValue() instanceof FilterField) {
                    Field _item = ((FilterField) ((CheckBoxTreeItem) item).getValue()).getField();
                    Dragboard db = filterFieldsTreeView.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.RTF, _item);
                    db.setContent(content);
                    draggedItem = _item;
                }
                t.consume();
            }
        });

        filterFieldsTreeView.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
//                System.out.println("" + t.getDragboard().hasRtf() + "  " + t.getDragboard().getContent(DataFormat.RTF));
                if (t.getGestureSource() != filterFieldsTreeView && draggedItem != null) {
//                        && t.getDragboard().hasRtf()
//                        && t.getDragboard().getContent(DataFormat.RTF) instanceof Field) {
                    Field field = draggedItem;//(Field) t.getDragboard().getContent(DataFormat.RTF);
                    if (!pivot.getSelectedFilterFields().contains(field)) {
                        t.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                }
                t.consume();
            }
        });

        filterFieldsTreeView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                boolean success = false;
                if (t.getGestureSource() != filterFieldsTreeView && draggedItem != null) {
//                        && t.getDragboard().hasRtf()
//                        && t.getDragboard().getContent(DataFormat.RTF) instanceof Field) {
                    Field field = draggedItem.getField();//(Field) t.getDragboard().getContent(DataFormat.RTF);
                    success = true;
                    try {
                        FilterField ff = new AbstractPivot.SimpleFilterField(pivot, field);
                        PivotUtils.removeUseOf(pivot, ff.getField());
                        pivot.validateFilterField(ff.getField());
                        pivot.getSelectedFilterFields().add(ff);

                        CheckBoxTreeItem item = new CheckBoxTreeItem(ff, null, true);
                        treeRoot.getChildren().add(item);

                        for (Object v : ff.getAllValues()) {
                            final CheckBoxTreeItem value = new CheckBoxTreeItem(v, null, true);
                            value.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> pv, Boolean ov, Boolean nv) {
                                    if (nv) {
                                        ff.getRestrictedValues().remove(v);
                                    } else {
                                        ff.getRestrictedValues().add(v);
                                    }
                                }
                            });
                            item.getChildren().add(value);
                        }

                        draggedItem = null;
                    } catch (Exception ex) {
                        AlertDialog.showAndWait(allFieldsListView.getParent().getScene(), ex.getMessage(), AlertDialog.ICON_ERROR);
                    } finally {
                        factory.refreshCells();
                    }
                }
                t.setDropCompleted(success);
                t.consume();
            }
        });
    }

    private void initializeListView(ListView lv, ObservableList list) {
        lv.setItems(list);

        lv.setEditable(true);
        lv.setCellFactory(factory);
        lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        registerDragAndDropHandlers(lv);
    }

    private void registerDragAndDropHandlers(final ListView<? extends Field> listView) {
        listView.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Field item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    item = item.getField();
                    Dragboard db = listView.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.RTF, item);
                    db.setContent(content);
                    draggedItem = item;
                }
                t.consume();
            }
        });

        listView.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
//                System.out.println("" + t.getDragboard().hasRtf() + "  " + t.getDragboard().getContent(DataFormat.RTF));
                if (t.getGestureSource() != listView && draggedItem != null) {
//                        && t.getDragboard().hasRtf()
//                        && t.getDragboard().getContent(DataFormat.RTF) instanceof Field) {
                    Field field = draggedItem;//(Field) t.getDragboard().getContent(DataFormat.RTF);
                    if (listView == allFieldsListView || listView == valuesFieldsListView || !listView.getItems().contains(field)) {
                        t.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                }
                t.consume();
            }
        });

        listView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent t) {
                boolean success = false;
                if (t.getGestureSource() != listView && draggedItem != null) {
//                        && t.getDragboard().hasRtf()
//                        && t.getDragboard().getContent(DataFormat.RTF) instanceof Field) {
                    Field field = draggedItem.getField();//(Field) t.getDragboard().getContent(DataFormat.RTF);
                    if (listView == allFieldsListView || listView == valuesFieldsListView || !listView.getItems().contains(field)) {
                        success = true;
                        if (listView == allFieldsListView) {
                            PivotUtils.removeUseOf(pivot, field);
                            factory.refreshCells();
                        } else {
                            try {
                                if (listView == valuesFieldsListView) {
                                    PivotUtils.removeUseOfNonValueField(pivot, field);
                                    AbstractPivot.SimpleAggregatedField av = new AbstractPivot.SimpleAggregatedField(pivot, field, idRunner++);
                                    pivot.validateValuesField(av);
                                    ((ObservableList<AggregatedField>) listView.getItems()).add(av);
                                } else {
                                    PivotUtils.removeUseOf(pivot, field);
                                    if (listView == columnsFieldsListView) {
                                        pivot.validateSeriesField(field);
                                    }
                                    if (listView == rowsFieldsListView) {
                                        pivot.validateAxisField(field);
                                    }
                                    ((ObservableList<Field>) listView.getItems()).add(field);
                                }
                                draggedItem = null;
                            } catch (Exception ex) {
                                AlertDialog.showAndWait(allFieldsListView.getParent().getScene(), ex.getMessage(), AlertDialog.ICON_ERROR);
                            } finally {
                                factory.refreshCells();
                            }
                        }
                    }
                }
                t.setDropCompleted(success);
                t.consume();
            }
        });

    }

    class ListCellFactory implements Callback {

        List<FieldListCell> existingCells = new LinkedList<>();

        public Scene getScene() {
            return allFieldsListView.getParent().getScene();
        }

        @Override
        public Object call(Object p) {
            FieldListCell cell = new FieldListCell(p == allFieldsListView, p == valuesFieldsListView, pivot, this);
            existingCells.add(cell);
            return cell;
        }

        public void refreshCells() {
            existingCells.stream().forEach((l) -> l.update());
        }
    };
}
