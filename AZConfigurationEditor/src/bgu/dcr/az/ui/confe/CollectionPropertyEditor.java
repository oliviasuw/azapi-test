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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
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

    private final Label infoContainer;
    private final ToolBar tools;
    private final VBox vBox;

    private Property collectionProperty;
    private boolean readOnly;
    private boolean editEnabled;

    private final NavigatableConfigurationEditor navigator;

    public CollectionPropertyEditor(NavigatableConfigurationEditor navigator) {
        this.navigator = navigator;

        getStyleClass().add("collection-property-editor");

        addButton = new Button("Add");
        addButton.setOnAction((e) -> onAddButton());
        editButton = new Button("Edit");
        editButton.setOnAction((e) -> onEditButton());
        clearButton = new Button("Remove all");
        clearButton.setOnAction((e) -> onClearButton());

        infoContainer = new Label("");
        tools = new ToolBar(addButton, editButton, clearButton);

        vBox = new VBox();
        vBox.getChildren().addAll(tools);
        setContent(vBox);
        setGraphic(infoContainer);
        setExpanded(false);
        editEnabled = false;
    }

    @Override
    public void setModel(Property property, boolean readOnly) {
        this.readOnly = readOnly;

        if (property == null) {
            collectionProperty = null;
            PropertyEditor.updateInfo(infoContainer, collectionProperty);
            return;
        }

        if (collectionProperty == property) {
            return;
        }

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
        fc.forEach((e) -> addItemToCollection(e, false));

        PropertyEditor.updateInfo(infoContainer, collectionProperty);
    }

    private void addItemToCollection(PropertyValue value, boolean newValue) {
        Class type = collectionProperty.typeInfo().getGenericParameters().get(0).getType();
        ValueComparablePseudoProperty pseudoProperty = new ValueComparablePseudoProperty("item", type);
        pseudoProperty.set(value);

        BorderPane grid = new BorderPane();
        Node editor = propertyToEditor(pseudoProperty);
        ((PropertyEditor) editor).setModel(pseudoProperty, readOnly);
        if (editor instanceof ConfigurationPropertyEditor) {
            ((ConfigurationPropertyEditor)editor).setExpanded(true);
        }
        Button remove = new Button("");
        remove.setGraphic(new ImageView(REMOVE_IMAGE));
        remove.setManaged(editEnabled);
        remove.setVisible(editEnabled);
        remove.setOnAction((ActionEvent t) -> {
            FromCollectionPropertyValue fcpv = (FromCollectionPropertyValue) collectionProperty.get();
            fcpv.remove(value);
            if (navigator != null) {
                navigator.removeSubTree(pseudoProperty);
            }
            vBox.getChildren().remove(grid);
        });
        BorderPane.setAlignment(remove, Pos.CENTER);
        grid.setLeft(remove);
        grid.setCenter(editor);
//        if (navigator != null) {
//            navigator.removeSubTree(pseudoProperty);
//        }
        vBox.getChildren().add(grid);
        if (navigator != null && newValue) {
            navigator.addCollectionItemTreeNode(collectionProperty, pseudoProperty);
        }
    }

    public final void onAddButton() {
        FromCollectionPropertyValue fcpv = (FromCollectionPropertyValue) collectionProperty.get();
        PropertyValue propertyValue = getPropertyValue();
        if (propertyValue != null) {
            fcpv.add(propertyValue);
            addItemToCollection(propertyValue, true);
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
        setModel(temp, readOnly);

        if (navigator != null) {
            navigator.removeChildren(collectionProperty);
        }
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
                Collection<Class> implementors = RegisteryUtils.getDefaultRegistery().getImplementors(implementorType);
                if (implementors.isEmpty()) {
                    return null;
                }
                try {
                    Configuration conf = RegisteryUtils.getDefaultRegistery().getConfiguration(implementors.iterator().next());
                    return new FromConfigurationPropertyValue(conf);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(CollectionPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        }
    }

    public Node propertyToEditor(Property property) {
        if (PropertyUtils.isPrimitive(property)) {
            return new TerminalPropertyEditor();
        } else {
            if (PropertyUtils.isCollection(property)) {
                return new CollectionPropertyEditor(navigator);
            } else {
                return new ConfigurationPropertyEditor(navigator, true);
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

    private static class ValueComparablePseudoProperty extends PropertyImpl {

        public ValueComparablePseudoProperty(String name, Class type) {
            super(name, null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValueComparablePseudoProperty && ((ValueComparablePseudoProperty) obj).get() == get();
        }

        @Override
        public String toString() {
            return "ValueComparablePseudoProperty{" + '}';
        }

        @Override
        public int hashCode() {
            return get() == null? 7 : get().hashCode();
        }

        
        
    }
}
