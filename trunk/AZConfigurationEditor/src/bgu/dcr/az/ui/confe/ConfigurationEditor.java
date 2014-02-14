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

    public void setModel(Configuration conf, boolean readOnly) {
        getChildren().clear();

        setSpacing(3);
        setPadding(new Insets(5));

        Collection<Property> properties = conf.properties();
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

        for (Property property : properties) {
            if (!PropertyUtils.isPrimitive(property) && !PropertyUtils.isCollection(property)) {
                ConfigurationPropertyEditor editor = new ConfigurationPropertyEditor();
                editor.setModel(property, readOnly);
                getChildren().add(editor);
            }
        }

        for (Property property : properties) {
            if (PropertyUtils.isCollection(property)) {
                CollectionPropertyEditor editor = new CollectionPropertyEditor();
                editor.setModel(property, readOnly);
                getChildren().add(editor);
            }
        }
    }

}
