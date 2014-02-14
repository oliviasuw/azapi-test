/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.utils.PropertyUtils;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 *
 * @author Shl
 */
public class CollectionListCell extends ListCell<Property> {

//    private TerminalPropertyEditorController controller;
    private PropertyEditor editor;
    private final boolean readOnly;

    public CollectionListCell(boolean readOnly) {
        this.readOnly = readOnly;
        setEditable(true);
        setDisable(readOnly);
    }

    @Override
    protected void updateItem(Property p, boolean empty) {
        if (p == null || empty) {
            setGraphic(null);
        } else {
            if (editor == null) {
                editor = propertyToPath(p);
            }
            if (getItem() != p) {
                editor.setModel(p, readOnly);
            }
            setGraphic((Node) editor);
        }
        super.updateItem(p, empty); //To change body of generated methods, choose Tools | Templates.
    }

    public PropertyEditor propertyToPath(Property property) {
        if (PropertyUtils.isPrimitive(property)) {
            return new TerminalPropertyEditor();
        } else {
            if (PropertyUtils.isCollection(property)) {
                return new CollectionPropertyEditor();
            } else {
                return new ConfigurationPropertyEditor();
            }
        }
    }

}
