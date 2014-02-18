/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.utils.EventListeners;
import bgu.dcr.az.ui.util.FXUtils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;

/**
 *
 * @author User
 */
public class ConfigurationEditor extends ScrollPane {

    private final ConfigurationEditorInternal internal;
    private Configuration model;
    private final EventListeners<ConfigurationEditorListener> listeners = EventListeners.create(ConfigurationEditorListener.class);
    private Node selectedNode = null;

    public ConfigurationEditor() {
        internal = new ConfigurationEditorInternal(this);

        this.sceneProperty().addListener((ov, o, n) -> initializeFocuseListening());

        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setFitToWidth(true);
        this.setContent(internal);

    }

    public EventListeners<ConfigurationEditorListener> getListeners() {
        return listeners;
    }

    public void setModel(Configuration model, boolean readOnly) {
        this.model = model;
        internal.setModel(model, readOnly);
        initializeFocuseListening();
    }

    public void select(Object item) {
        Node child = FXUtils.lookupChild(internal, t -> {
            if (t instanceof PropertyEditor) {
                Property model = ((PropertyEditor) t).getModel();
                return model == item || model.get() == item;
            }
            return false;
        });
        
        if (item == model) {
            child = internal;
        }
        
        if (child != null) {
            selectNode(child, true);
        }
    }

    private void initializeFocuseListening() {
        if (getScene() == null) {
            System.out.println("scene not found!");
            return;
        }

        getScene().focusOwnerProperty().addListener((ov, o, n) -> selectNode(n, false));
    }

    private void selectNode(Node n, boolean expand) {
        if (selectedNode == n) {
            return;
        }

        TitledPane newSelection = (TitledPane) FXUtils.lookupParent(n, node -> node instanceof TitledPane);

        //expand new selection: 
        if (newSelection != null && expand) {
            FXUtils.lookupParents(newSelection, node -> node instanceof TitledPane, node -> node == internal)
                    .forEach(node -> {
                        TitledPane tnode = (TitledPane) node;
                        tnode.setExpanded(true);
                    });
        }

        if (selectedNode != newSelection) {

            if (newSelection != null && selectedNode != null) {
                selectedNode.getStyleClass().remove("selected");
            }

            if (newSelection != null) {
                newSelection.getStyleClass().add("selected");
                selectedNode = newSelection;
            }

            if (newSelection instanceof PropertyEditor) {
                Property model = ((PropertyEditor) newSelection).getModel();
                if (model.get() != null && model.parent() == null) {
                    getListeners().fire().onItemSelecttion(this, model.get());
                } else {
                    getListeners().fire().onItemSelecttion(this, model);
                }
            }
        }

        if (newSelection != null) {
            FXUtils.ensureVisibility(this, newSelection, false);
        }
        
        if (n == internal) {
            FXUtils.ensureVisibility(this, n, false);
        }
    }

    public interface ConfigurationEditorListener {

        void onPropertyValueChanged(ConfigurationEditor source, Property property);

        void onPropertyValueAdded(ConfigurationEditor source, Property collection, PropertyValue value);

        void onPropertyValueRemoved(ConfigurationEditor source, Property collection, PropertyValue value);

        void onItemSelecttion(ConfigurationEditor source, Object item);
    }

}
