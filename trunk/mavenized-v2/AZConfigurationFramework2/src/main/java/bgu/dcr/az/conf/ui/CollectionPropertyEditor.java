/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.ui;

import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.conf.ConfigurableTypeInfoImpl;
import bgu.dcr.az.conf.FromCollectionPropertyValue;
import bgu.dcr.az.conf.FromConfigurationPropertyValue;
import bgu.dcr.az.conf.FromStringPropertyValue;
import bgu.dcr.az.conf.PropertyImpl;
import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.api.PropertyValue;
import bgu.dcr.az.conf.registery.Registery;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.conf.utils.PropertyUtils;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import resources.img.ResourcesImg;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class CollectionPropertyEditor extends TitledPane implements PropertyEditor, Selectable {

    public static final Image REMOVE_IMAGE = ResourcesImg.png("remove");

    private final ConfigurationEditor parent;

    private final Button addButton;
    private final Button editButton;
    private final Button clearButton;

    private final Label infoContainer;
    private final ToolBar tools;
    private final VBox vBox;

    private Property collectionProperty;
    private boolean readOnly;
    private boolean editEnabled;

    private Predicate filter;

    private final BooleanProperty selected;

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

//    private final NavigatableConfigurationEditor navigator;
    public CollectionPropertyEditor(ConfigurationEditor parent) {
        this.parent = parent;
        getStyleClass().add("collection-property-editor");
        selected = new SimpleBooleanProperty(false);

        skinProperty().addListener((sp, o, n) -> {
            Node node = FXUtils.getTitledPaneTitleRegion(this);

            if (node != null) {
                node.addEventFilter(MouseEvent.MOUSE_RELEASED, eh -> eh.consume());
                node.addEventFilter(MouseEvent.MOUSE_PRESSED, eh -> {
                    eh.consume();
                    if (!selected.get()) {
                        selected.set(true);
                    } else {
                        setExpanded(!isExpanded());
                    }
                });
            }
        });

        selected.addListener((p, ov, nv) -> {
            if (nv && parent != null) {
                parent.select(this);
            }
        });

        addButton = new Button("Add");
        addButton.setOnAction((e) -> onAddButton());
        editButton = new Button("Edit");
        editButton.setOnAction((e) -> onEditButton());
        clearButton = new Button("Remove all");
        clearButton.setOnAction((e) -> onClearButton());

        infoContainer = new Label("");
        tools = new ToolBar(addButton, editButton, clearButton);
        tools.getStyleClass().add("buttons");

        vBox = new VBox();
        vBox.getChildren().addAll(tools);
        setContent(vBox);
        setGraphic(infoContainer);
        setExpanded(false);
        editEnabled = false;
    }

    @Override
    public void setModel(Property property, boolean readOnly, Predicate filter) {
        this.readOnly = readOnly;

        if (property == null) {
            collectionProperty = null;
            PropertyEditor.updateInfo(infoContainer, collectionProperty);
            return;
        }

        if (collectionProperty == property) {
            return;
        }

        this.filter = filter;
        this.collectionProperty = property;
        setText("Collection of " + property.name());

        vBox.getChildren().removeAll(vBox.getChildren());
        if (!readOnly) {
            vBox.getChildren().add(tools);
        }

        if (property.get() == null) {
            property.set(new FromCollectionPropertyValue());
        }

        FromCollectionPropertyValue fc = (FromCollectionPropertyValue) property.get();
        fc.forEach(this::addItemToCollection);

        PropertyEditor.updateInfo(infoContainer, collectionProperty);
    }

    private void addItemToCollection(PropertyValue value) {
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        PropertyImpl pseudoProperty = new PropertyImpl("item", null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""), 0, 0);
        pseudoProperty.set(value);

        BorderPane grid = new BorderPane();
        Node editor = propertyToEditor(pseudoProperty);
        ((PropertyEditor) editor).setModel(pseudoProperty, readOnly, filter);
        if (editor instanceof ConfigurationPropertyEditor) {
            ((ConfigurationPropertyEditor) editor).setExpanded(true);
        }
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
        if (propertyValue != null) {
            fcpv.add(propertyValue);
            addItemToCollection(propertyValue);
        }
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
        setModel(temp, readOnly, filter);
    }

    public PropertyValue getPropertyValue() {
        final Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        if (PropertyUtils.isPrimitive(type)) {
            return new FromStringPropertyValue(null);
        } else {
            if (PropertyUtils.isCollection(type)) {
                return new FromCollectionPropertyValue();
            } else {
                Class implementorType = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
                Collection<Class> implementors = Registery.get().getImplementors(implementorType);
                if (implementors.isEmpty()) {
                    return null;
                }
                try {
                    Configuration conf = Registery.get().getConfiguration(implementors.iterator().next());
                    return new FromConfigurationPropertyValue(conf);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(CollectionPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        }
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly, Predicate filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Property getModel() {
        return collectionProperty;
    }

    private Node propertyToEditor(Property property) {
        if (PropertyUtils.isPrimitive(property)) {
            return new TerminalPropertyEditor(parent, null);
        } else {
            if (PropertyUtils.isCollection(property)) {
                return new CollectionPropertyEditor(parent);
            } else {
                return new ConfigurationPropertyEditor(parent, collectionProperty);
            }
        }
    }

}
