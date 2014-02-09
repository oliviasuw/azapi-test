/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.conf.impl.ConfigurableTypeInfoImpl;
import bgu.dcr.az.anop.conf.impl.FromCollectionPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.anop.conf.impl.PropertyImpl;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.net.URL;
import java.util.Iterator;
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
    private ObservableList<Property> values;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    public void setModel(final Property property, final boolean readOnly) {
        this.collectionProperty = property;
        title.setText(property.name());
        String description = property.doc().description();
        if (!description.isEmpty()) {
            title.setTooltip(new Tooltip(description));
        } else {
            title.setTooltip(null);
        }

        PropertyValue propertyValue = property.get();
        final FromCollectionPropertyValue fc = (propertyValue == null) ? new FromCollectionPropertyValue() : (FromCollectionPropertyValue) propertyValue;

        property.set(fc);
        values = FXCollections.observableArrayList();

        //in case there are already values in fc
        Iterator<PropertyValue> it = fc.iterator();
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        while (it.hasNext()) {
            PropertyImpl innerProperty = new PropertyImpl("", null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
            innerProperty.set(it.next());
            values.add(innerProperty);
        }

        listView.setItems(values);
        listView.setCellFactory(new Callback() {
            @Override
            public Object call(Object p) {
                return new CollectionListCell(property, readOnly);
            }
        });

        values.addListener(new ListChangeListener<Property>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Property> change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends Property> removed = change.getRemoved();
                        for (Property val : removed) {
                            fc.remove(val.get());
                            System.out.println("Remove the value " + val.stringValue() + " from fc");
                        }
                    } else if (change.wasAdded()) {
                        List<? extends Property> added = change.getAddedSubList();
                        for (Property a : added) {
                            fc.add(a.get());
                        }
                    } else if (change.wasUpdated()) {
                        //should i delete and add again or changes will automatically reflect?
                    } else if (change.wasReplaced()) {
                        //what to do?
                    }
                }
            }
        });
        
        clearButton.setDisable(readOnly);
        removeButton.setDisable(readOnly);
        addButton.setDisable(readOnly);


    }

    public void onAddButton() {
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        PropertyImpl pseudo = new PropertyImpl("", null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
        if (PropertyUtils.isPrimitive(type)) {
            pseudo.set(new FromStringPropertyValue(""));
        } else if (PropertyUtils.isCollection(type)) {
            pseudo.set(new FromCollectionPropertyValue());
        } else {
            pseudo.set(new FromConfigurationPropertyValue(null));
        }
        values.add(pseudo);

    }

    public void onRemoveButton() {
        ObservableList selectedItems = listView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0) {
//            warningText.setText("No selection found!");
        } else {
            for (Object s : selectedItems.toArray()){
               values.remove(s); 
                System.out.println("removed: " + s);
            }
        }
    }

    public void onClearButton() {
        listView.getSelectionModel().selectAll();
        onRemoveButton();
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
