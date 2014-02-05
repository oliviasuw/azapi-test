/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.conf.impl.FromCollectionPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class CollectionPropertyEditorController implements Initializable, PropertyController {

    @FXML
    private TitledPane title;
    @FXML
    private ListView listView;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;

    private Property collectionProperty;
    private ObservableList<PropertyValue> values;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    public void setModel(final Property property) {
        this.collectionProperty = property;
        title.setText(property.name());
        String description = property.doc().description();
        if (!description.isEmpty()) {
            title.setTooltip(new Tooltip(description));
        } else {
            title.setTooltip(null);
        }
        final FromCollectionPropertyValue fc = new FromCollectionPropertyValue();
        property.set(fc);
        values = FXCollections.observableArrayList();
        listView.setItems(values);
        listView.setCellFactory(new Callback() {
            @Override
            public Object call(Object p) {
                return new CollectionListCell(property);
            }
        });

        values.addListener(new ListChangeListener<PropertyValue>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends PropertyValue> change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends PropertyValue> removed = change.getRemoved();
                        for (PropertyValue val : removed) {
                            fc.remove(val);
                        }
                    } else if (change.wasAdded()) {
                        List<? extends PropertyValue> added = change.getAddedSubList();
                        fc.addAll(values);
                    } else if (change.wasUpdated()) {
                        //should i delete and add again or changes will automatically reflect?
                    } else if (change.wasReplaced()) {
                        //what to do?
                    }
                }
            }
        });

    }

    public void onAddButton() {
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        if (PropertyUtils.isPrimitive(type)) {
            values.add(new FromStringPropertyValue(""));
        } else if (PropertyUtils.isCollection(type)) {
            values.add(new FromCollectionPropertyValue());
        } else {
            values.add(new FromConfigurationPropertyValue(null));
        }

    }

    public void onRemoveButton() {
        ObservableList selectedItems = listView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0) {
//            warningText.setText("No selection found!");
        } else {
            values.removeAll(selectedItems);
//            listView.getSelectionModel().clearSelection();
        }
    }

    public void onClearButton() {
        listView.getSelectionModel().selectAll();
        onRemoveButton();
    }

    @Override
    public void setModel(Configuration conf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
