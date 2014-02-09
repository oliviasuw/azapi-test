/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 *
 * @author Shl
 */
public class CollectionListCell extends ListCell {

    private final Property collectionProperty;
    private Node node = null;
//    private TerminalPropertyEditorController controller;
    private PropertyController controller;
    private boolean readOnly;

    public CollectionListCell(Property collectionProperty, boolean readOnly) {
        this.collectionProperty = collectionProperty;
        this.readOnly = readOnly;
    }

    @Override
    protected void updateItem(Object t, boolean empty) {
        super.updateItem(t, empty); //To change body of generated methods, choose Tools | Templates.
        if (t == null || empty) {
            setGraphic(null);
        } else {
            Property p = (Property) t;
            if (node == null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(propertyToPath(p)));
                    node = (Node) loader.load();
                    controller = (PropertyController) loader.getController();
                    controller.setModel(p, readOnly);
                    setGraphic(node);
                } catch (IOException ex) {
                    System.out.println("exception in fxmlloader");
                }
            } else {
//                  property.set(propertyValue);
//                controller.setModel(property);
//                setGraphic(node);
            }

        }
    }

    public String propertyToPath(Property property) {
        if (PropertyUtils.isPrimitive(property)) {
            return "TerminalPropertyEditor.fxml";
        } else {
            if (!PropertyUtils.isCollection(property)) {
                return "ConfigurationPropertyEditor.fxml";
            } else {
                return "CollectionPropertyEditor.fxml";
            }
        }
    }

}
