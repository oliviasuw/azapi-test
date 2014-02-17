/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.util.Collection;
import java.util.LinkedList;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class ConfigurationEditor extends VBox {

    private final NavigatableConfigurationEditor navigator;
    
    private Configuration configuration;
    private boolean readOnly;

    public ConfigurationEditor(NavigatableConfigurationEditor navigator) {
        this.navigator = navigator;
        setSpacing(3);
        setPadding(new Insets(5));
        
        getStyleClass().add("conf-editor");
    }

    public void setModel(Configuration configuration, boolean readOnly) {
        this.readOnly = readOnly;

        if (this.configuration == configuration) {
            return;
        }

        this.configuration = configuration;

        getChildren().clear();

        if (configuration == null) {
            return;
        }

        Collection<Property> properties = configuration.properties();

        generateTerminalPropertiesEditors(properties);
        generateConfigurationPropertiesEditors(properties);
        generateCollectionPropertiesEditors(properties);
    }

    private void generateCollectionPropertiesEditors(Collection<Property> properties) {
        for (Property property : properties) {
            if (PropertyUtils.isCollection(property)) {
                if (property.doc() != null
                        && "false".equals(property.doc().first("UIVisibility"))) {
                    continue;
                }
                CollectionPropertyEditor editor = new CollectionPropertyEditor(navigator);
                editor.setModel(property, readOnly);
                getChildren().add(editor);
            }
        }
    }

    private void generateConfigurationPropertiesEditors(Collection<Property> properties) {
        for (Property property : properties) {
            if (!PropertyUtils.isPrimitive(property) && !PropertyUtils.isCollection(property)) {
                if (property.doc() != null
                        && "false".equals(property.doc().first("UIVisibility"))) {
                    continue;
                }
                ConfigurationPropertyEditor editor = new ConfigurationPropertyEditor(navigator, false);
                editor.setModel(property, readOnly);
                getChildren().add(editor);
            }
        }
    }

    private void generateTerminalPropertiesEditors(Collection<Property> properties) {
        double max = 0;
        LinkedList<TerminalPropertyEditor> controllerList = new LinkedList<>();
        for (Property property : properties) {
            if (PropertyUtils.isPrimitive(property)) {
                if (property.doc() != null
                        && property.doc().first("UIVisibility") != null
                        && property.doc().first("UIVisibility").toLowerCase().equals("false")) {
                    continue;
                }

                TerminalPropertyEditor controller = new TerminalPropertyEditor();
                controller.setModel(property, readOnly);
                double labelWidth = controller.getLabelWidth();
                if (labelWidth > max) {
                    max = labelWidth;
                }
                controllerList.add(controller);
                getChildren().add(controller);
            }
        }
        for (TerminalPropertyEditor controller : controllerList) {
            controller.setLabelWidth(max);
        }
    }
}
