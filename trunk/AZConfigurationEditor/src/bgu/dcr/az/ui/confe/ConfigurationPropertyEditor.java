/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class ConfigurationPropertyEditor extends TitledPane implements PropertyEditor {

    private static final int LABEL_MARGING = 10;

    private final ConfigurationEditor parent;

    private final ConfigurationEditorInternal confEditor;
    private final ChoiceBox<String> choiceBox;
    private final Label infoContainer;
    private Property property;
    private final BorderPane implementorsBorderPane;
    private boolean readOnly;
    private final boolean isListItem;
    private final VBox editorVBox;

    public ConfigurationPropertyEditor(ConfigurationEditor parent, boolean isListItem) {
        this.parent = parent;
        this.isListItem = isListItem;

        getStyleClass().add("configuration-property-editor");

        infoContainer = new Label("");
        confEditor = new ConfigurationEditorInternal(parent);

        implementorsBorderPane = new BorderPane();
        implementorsBorderPane.getStyleClass().add("implementors");

        Label label = new Label("Implementations :");
        label.setPadding(new Insets(0, LABEL_MARGING, 0, 0));
        BorderPane.setAlignment(label, Pos.CENTER_LEFT);
        implementorsBorderPane.setLeft(label);

        choiceBox = new ChoiceBox<>();
        choiceBox.setMaxWidth(Double.MAX_VALUE);
        choiceBox.valueProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> {
            try {
                if (property != null) {
                    Configuration value = null;
                    if (!nv.equals("NULL")) {
                        value = RegisteryUtils.getRegistery().getConfiguration(nv);
                    }
                    property.set(value == null ? null : new FromConfigurationPropertyValue(value));
                    parent.getListeners().fire().onPropertyValueChanged(parent, property);
                    confEditor.setModel(value, readOnly);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        implementorsBorderPane.setCenter(choiceBox);

        editorVBox = new VBox();
        editorVBox.getChildren().addAll(implementorsBorderPane, confEditor);

        setGraphic(infoContainer);
        setExpanded(false);
    }

    @Override
    public void setModel(Property property, boolean readOnly) {
        this.readOnly = readOnly;
        if (this.property != property) {
            setContent(null);
        }

        if (property == null) {
            this.property = null;
            PropertyEditor.updateInfo(infoContainer, this.property);
            return;
        }

        if (this.property == property) {
            return;
        }
        
        setText(property.name());

        editorVBox.getChildren().removeAll(implementorsBorderPane, confEditor);
        extractImplementors(property);
        this.property = property;
        updateModelValue();

        setContent(editorVBox);
        choiceBox.setDisable(readOnly);
        PropertyEditor.updateInfo(infoContainer, this.property);
        editorVBox.getChildren().add(confEditor);
    }

    private void updateModelValue() {
        if (property.get() == null) {
            try {
                String defaultName = (String) choiceBox.getItems().get(0);
                Configuration defaultValue = RegisteryUtils.getRegistery().getConfiguration(defaultName);
                property.set(new FromConfigurationPropertyValue(defaultValue));
            } catch (Exception ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FromConfigurationPropertyValue fcpv = (FromConfigurationPropertyValue) property.get();

        if (fcpv != null && fcpv.getValue() != null) { //in case property is a dummy (came from collection property editor)
            Class implementor = fcpv.getValue().typeInfo().getType();
            String className = RegisteryUtils.getRegistery().getRegisteredClassName(implementor);
            Property temp = property;
            property = null;
            choiceBox.getSelectionModel().select(className);
            property = temp;
            if (isListItem && choiceBox.getItems().size() <= 1) {
                setText(className);
            }
            confEditor.setModel(fcpv.getValue(), readOnly);
        } else {
            choiceBox.getSelectionModel().select("NULL");
        }
    }

    private void extractImplementors(Property property) {
        if (this.property == property) {
            return;
        }
        this.property = null;
        Class pType = property.typeInfo().getType();
        Collection<Class> implementors = RegisteryUtils.getRegistery().getImplementors(pType);
        choiceBox.getItems().clear();
        if (!isListItem) {
            choiceBox.getItems().add("NULL");
        }

        implementors.forEach((i) -> choiceBox.getItems().add(RegisteryUtils.getRegistery().getRegisteredClassName(i)));

        if (!isListItem || implementors.size() > 1) {
            editorVBox.getChildren().add(implementorsBorderPane);
        }
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Property getModel() {
        return property;
    }
}
