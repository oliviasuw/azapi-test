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
import bgu.dcr.az.anop.conf.impl.PropertyImpl;
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.PropertyUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class CollectionPropertyEditor extends TitledPane implements PropertyEditor {

    private ListView<Property> listView;
    private final Button addButton;
    private final Button removeButton;
    private final Button clearButton;

    private Property collectionProperty;
    private ObservableList<Property> values;
    private final ToolBar tools;
    private final VBox vBox;

    public CollectionPropertyEditor() {
        addButton = new Button("Add");
        addButton.setOnAction((e) -> onAddButton());
        removeButton = new Button("Remove");
        removeButton.setOnAction((e) -> onRemoveButton());
        clearButton = new Button("Remove all");
        clearButton.setOnAction((e) -> onClearButton());

        tools = new ToolBar(addButton, removeButton, clearButton);

        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setEditable(true);

        vBox = new VBox();
        vBox.getChildren().addAll(tools, listView);
        setContent(vBox);
        setExpanded(false);
    }

    @Override
    public void setModel(final Property property, final boolean readOnly) {
        if (readOnly) {
            vBox.getChildren().remove(tools);
        }
        this.collectionProperty = property;
        setText("Collection of " + property.name());
        String description = property.doc().description();
        if (!description.isEmpty()) {
            setTooltip(new Tooltip(description));
        } else {
            setTooltip(null);
        }

        if (property.get() == null) {
            property.set(new FromCollectionPropertyValue());
        }
        final FromCollectionPropertyValue fc = (FromCollectionPropertyValue) property.get();

        property.set(fc);
        vBox.getChildren().remove(listView);
        listView = new ListView<>();
        vBox.getChildren().add(listView);
        values = FXCollections.<Property>observableArrayList();

        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        for (PropertyValue prop : fc) {
            PropertyImpl innerProperty = new PropertyImpl("", null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
            innerProperty.set(prop);
            values.add(innerProperty);
        }

        listView.setItems(values);
        listView.setCellFactory(p -> new CollectionListCell(readOnly));

        values.addListener((ListChangeListener.Change<? extends Property> change) -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().stream().forEach(e -> fc.remove(e.get()));
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(e -> fc.add(e.get()));
                }
            }
        });

        clearButton.setDisable(readOnly);
        removeButton.setDisable(readOnly);
        addButton.setDisable(readOnly);
        listView.setDisable(readOnly);
    }

    public final void onAddButton() {
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        PropertyImpl pseudo = new PropertyImpl("", null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
        if (PropertyUtils.isPrimitive(type)) {
            pseudo.set(null);
        } else if (PropertyUtils.isCollection(type)) {
            pseudo.set(null);
        } else {
            pseudo.set(null);
        }
        values.add(pseudo);

    }

    public final void onRemoveButton() {
        for (Object e : listView.getSelectionModel().getSelectedItems().toArray()) {
            values.remove(e);
        }
    }

    public final void onClearButton() {
        listView.getSelectionModel().selectAll();
        onRemoveButton();
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
