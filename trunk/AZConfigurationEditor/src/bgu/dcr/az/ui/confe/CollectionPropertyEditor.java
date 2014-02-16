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
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.PropertyUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import resources.img.R;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class CollectionPropertyEditor extends TitledPane implements PropertyEditor {

    public static final Image REMOVE_IMAGE = new Image(R.class.getResourceAsStream("remove.png"));

    private final Button addButton;
    private final Button editButton;
    private final Button clearButton;

    private final ToolBar tools;
    private final VBox vBox;

    private Property collectionProperty;
    private boolean readOnly;
    private boolean editEnabled;

    public CollectionPropertyEditor() {
        addButton = new Button("Add");
        addButton.setOnAction((e) -> onAddButton());
        editButton = new Button("Edit");
        editButton.setOnAction((e) -> onEditButton());
        clearButton = new Button("Remove all");
        clearButton.setOnAction((e) -> onClearButton());

        tools = new ToolBar(addButton, editButton, clearButton);

        vBox = new VBox();
        vBox.getChildren().addAll(tools);
        setContent(vBox);
        setExpanded(false);
        editEnabled = false;
    }

    @Override
    public void setModel(final Property property, final boolean readOnly) {
        this.readOnly = readOnly;
        if (collectionProperty == property) {
            return;
        }
        this.collectionProperty = property;
        setText("Collection of " + property.name());
        String description = property.doc().description();
        if (!description.isEmpty()) {
            Label image = new Label("", new ImageView(ConfigurationEditor.INFO_ICON));
            image.setTooltip(new Tooltip(description));
            setGraphic(image);
        } else {
            setGraphic(null);
        }

        if (property.get() == null) {
            property.set(new FromCollectionPropertyValue());
        }
        FromCollectionPropertyValue fc = (FromCollectionPropertyValue) property.get();

        vBox.getChildren().removeAll(vBox.getChildren());
        if (!readOnly) {
            vBox.getChildren().add(tools);
        }

        fc.forEach(this::addItemToCollection);

//        values.addListener((ListChangeListener.Change<? extends Property> change) -> {
//            while (change.next()) {
//                if (change.wasRemoved()) {
//                    change.getRemoved().stream().forEach(e -> fc.remove(e.get()));
//                } else if (change.wasAdded()) {
//                    change.getAddedSubList().forEach(e -> fc.add(e.get()));
//                }
//            }
//        });
    }

    private void addItemToCollection(PropertyValue value) {
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        PropertyImpl pseudoProperty = new PropertyImpl("item", null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
        BorderPane grid = new BorderPane();
        Node editor = propertyToEditor(pseudoProperty);
        ((PropertyEditor) editor).setModel(pseudoProperty, readOnly);
        Button remove = new Button("");
        remove.setGraphic(new ImageView(REMOVE_IMAGE));
        remove.setManaged(editEnabled);
        remove.setVisible(editEnabled);
        remove.setOnAction((ActionEvent t) -> {
            FromCollectionPropertyValue fcpv = (FromCollectionPropertyValue) collectionProperty.get();
            fcpv.remove(value);
            vBox.getChildren().remove(grid);
        });
        BorderPane.setAlignment(remove, Pos.CENTER);
        grid.setLeft(remove);
        grid.setCenter(editor);
        vBox.getChildren().add(grid);
    }

    public final void onAddButton() {
        FromCollectionPropertyValue fcpv = (FromCollectionPropertyValue) collectionProperty.get();
        PropertyValue propertyValue = getPropertyValue();
        fcpv.add(propertyValue);
        addItemToCollection(propertyValue);
    }

    public final void onEditButton() {
        editEnabled = !editEnabled;
        for (Node c : vBox.getChildren()) {
            if (c instanceof BorderPane) {
                Button editBtn = (Button) ((BorderPane) c).getLeft();
                editBtn.setManaged(editEnabled);
                editBtn.setVisible(editEnabled);
            }
        }
    }

    public final void onClearButton() {
        collectionProperty.set(new FromCollectionPropertyValue());
        Property temp = collectionProperty;
        collectionProperty = null;
        setModel(temp, readOnly);
    }

    public PropertyValue getPropertyValue() {
        final Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        if (PropertyUtils.isPrimitive(type)) {
            return new FromStringPropertyValue(null);
        } else {
            if (PropertyUtils.isCollection(type)) {
                return new FromCollectionPropertyValue();
            } else {
                return new FromConfigurationPropertyValue(null);
            }
        }
    }

    public Node propertyToEditor(Property property) {
        if (PropertyUtils.isPrimitive(property)) {
            return new TerminalPropertyEditor();
        } else {
            if (PropertyUtils.isCollection(property)) {
                return new CollectionPropertyEditor();
            } else {
                return new ConfigurationPropertyEditor();
            }
        }
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Property getModel() {
        return collectionProperty;
    }
}
