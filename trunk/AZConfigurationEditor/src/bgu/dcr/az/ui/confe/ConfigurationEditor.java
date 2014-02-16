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

    private Configuration configuration;
    private boolean readOnly;

    public ConfigurationEditor() {
        setSpacing(3);
        setPadding(new Insets(5));
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
                CollectionPropertyEditor editor = new CollectionPropertyEditor();
                editor.setModel(property, readOnly);
                getChildren().add(editor);
            }
        }
    }

    private void generateConfigurationPropertiesEditors(Collection<Property> properties) {
        for (Property property : properties) {
            if (!PropertyUtils.isPrimitive(property) && !PropertyUtils.isCollection(property)) {
                ConfigurationPropertyEditor editor = new ConfigurationPropertyEditor(false);
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
