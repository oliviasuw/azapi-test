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
    private final ConfigurationEditor confEditor;
    private final ChoiceBox<String> choiceBox;
    private final Label infoContainer;
    private final Label itemLabel;
    private Property property;
    private final BorderPane implementorsBorderPane;
    private boolean readOnly;
    private final boolean isListItem;
    private final VBox editorVBox;
    
    private final NavigatableConfigurationEditor navigator;

    public ConfigurationPropertyEditor(NavigatableConfigurationEditor navigator, boolean isListItem) {
        this.navigator = navigator;
        this.isListItem = isListItem;

        getStyleClass().add("configuration-property-editor");
        
        infoContainer = new Label("");
        confEditor = new ConfigurationEditor(navigator);

        implementorsBorderPane = new BorderPane();
        implementorsBorderPane.getStyleClass().add("implementors");

        Label label = new Label("Implementations :");
        label.setPadding(new Insets(0, LABEL_MARGING, 0, 0));
        BorderPane.setAlignment(label, Pos.CENTER_LEFT);
        implementorsBorderPane.setLeft(label);

        itemLabel = new Label();

        choiceBox = new ChoiceBox<>();
        choiceBox.setMaxWidth(Double.MAX_VALUE);
        choiceBox.valueProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> {
            try {
                if (property != null) {
                    Configuration value = null;
                    if (!nv.equals("NULL")) {
                        value = RegisteryUtils.getDefaultRegistery().getConfiguration(nv);
                    }
                    property.set(value == null ? null : new FromConfigurationPropertyValue(value));
                    if (navigator != null) {
                        navigator.addFromConfigurationTreeNodes(property);
                    }
                    confEditor.setModel(value, readOnly);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

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
            updateInfo(infoContainer);
            return;
        }
        
        if (this.property == property) {
            return;
        }

        extractImplementors(property);
        this.property = property;
        updateModelValue();

        setText(property.name());
        setContent(editorVBox);
        choiceBox.setDisable(readOnly);
        updateInfo(infoContainer);
    }

    private void updateModelValue() {
        if (property.get() == null) {
            try {
                String defaultName = (String) choiceBox.getItems().get(0);
                Configuration defaultValue = RegisteryUtils.getDefaultRegistery().getConfiguration(defaultName);
                property.set(new FromConfigurationPropertyValue(defaultValue));
            } catch (Exception ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FromConfigurationPropertyValue confv = (FromConfigurationPropertyValue) property.get();
        if (confv != null && confv.getValue() != null) { //in case property is a dummy (came from collection property editor)
            Class implementor = confv.getValue().typeInfo().getType();
            String className = RegisteryUtils.getDefaultRegistery().getRegisteredClassName(implementor);
            choiceBox.getSelectionModel().select(className);
            itemLabel.setText(className);
            confEditor.setModel(confv.getValue(), readOnly);
        } else {
            choiceBox.getSelectionModel().select("NULL");
        }
    }

    private void extractImplementors(Property property) {
        if (this.property != property) {
            this.property = null;
            Class pType = property.typeInfo().getType();
            Collection<Class> implementors = RegisteryUtils.getDefaultRegistery().getImplementors(pType);
            choiceBox.getItems().clear();
            if (!isListItem) {
                choiceBox.getItems().add("NULL");
            }
            implementors.forEach((i) -> choiceBox.getItems().add(RegisteryUtils.getDefaultRegistery().getRegisteredClassName(i)));

            if (isListItem && implementors.size() <= 1) {
//                itemLabel.setAlignment(Pos.CENTER_LEFT);
                BorderPane.setAlignment(itemLabel, Pos.CENTER_LEFT);
                implementorsBorderPane.setCenter(itemLabel);
            } else {
                implementorsBorderPane.setCenter(choiceBox);
            }
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
