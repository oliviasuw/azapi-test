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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class ConfigurationPropertyEditor extends TitledPane implements PropertyEditor {

    private static final int LABEL_MARGING = 10;
    private final ConfigurationEditor confEditor;
    private ChoiceBox<String> choiceBox;
    private Property property;
    private boolean readOnly;
    private boolean reset;
    private final GridPane gridPane;

    public ConfigurationPropertyEditor() {
        confEditor = new ConfigurationEditor();

        gridPane = new GridPane();
        gridPane.getRowConstraints().add(new RowConstraints(10, 30, Double.MAX_VALUE));
        final Label label = new Label("Implementations");
        label.setPadding(new Insets(0, LABEL_MARGING, 0, 0));
        gridPane.add(label, 0, 0);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(gridPane, confEditor);
        setContent(vBox);

        setExpanded(false);
    }

    @Override
    public void setModel(final Property property, final boolean readOnly) {
        this.property = property;
        this.readOnly = readOnly;
        setText(property.name());
        String description = property.doc().description();
        if (description != null && !description.isEmpty()) {
            Tooltip tooltip = new Tooltip(description);
            setTooltip(tooltip);
        }

        Class pType = property.typeInfo().getType();
        Collection<Class> implementors = RegisteryUtils.getDefaultRegistery().getImplementors(pType);

        reset = true;
        if (choiceBox != null) {
            gridPane.getChildren().remove(choiceBox);
        }
        choiceBox = new ChoiceBox<>();
        gridPane.add(choiceBox, 1, 0);
        implementors.forEach((i) -> choiceBox.getItems().add(RegisteryUtils.getDefaultRegistery().getRegisteredClassName(i)));
        reset = false;

        if (property.get() == null) {
            try {
                String defaultName = (String) choiceBox.getItems().get(0);
                Configuration defaultValue = RegisteryUtils.getDefaultRegistery().getConfiguration(defaultName);
                property.set(new FromConfigurationPropertyValue(defaultValue));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FromConfigurationPropertyValue confv = (FromConfigurationPropertyValue) property.get();
        if (confv != null && confv.getValue() != null) { //in case property is a dummy (came from collection property editor)
            Class implementor = confv.getValue().typeInfo().getType();
            String className = RegisteryUtils.getDefaultRegistery().getRegisteredClassName(implementor);
            choiceBox.getSelectionModel().select(className);
            confEditor.setModel(confv.getValue(), readOnly);
        }

        choiceBox.valueProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> {
            try {
                Configuration value = RegisteryUtils.getDefaultRegistery().getConfiguration(nv);
                property.set(new FromConfigurationPropertyValue(value));
                confEditor.setModel(value, readOnly);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        choiceBox.setDisable(readOnly);
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
