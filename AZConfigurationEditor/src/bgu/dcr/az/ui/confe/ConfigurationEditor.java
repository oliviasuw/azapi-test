/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
        try {

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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigurationPropertyEditor.fxml"));
                    Node node = (Node) loader.load();
                    ConfigurationPropertyEditorController controller = loader.getController();
                    controller.setModel(property, readOnly);
                    getChildren().add(node);
                }
            }

            for (Property property : properties) {
                if (PropertyUtils.isCollection(property)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectionPropertyEditor.fxml"));
                    Node node = (Node) loader.load();
                    CollectionPropertyEditorController controller = (CollectionPropertyEditorController) loader.getController();
                    controller.setModel(property, readOnly);
                    getChildren().add(node);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ConfigurationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
