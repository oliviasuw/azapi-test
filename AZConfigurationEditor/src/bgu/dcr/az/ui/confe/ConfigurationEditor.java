/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.common.ui.FXUtils;
import java.util.function.Predicate;
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
    private Node selectedNode = null;
    private boolean autoScroll = true;

    public ConfigurationEditor() {
        this(true);
    }

    public ConfigurationEditor(boolean autoScroll) {
        getStyleClass().add("conf-editor-scroll");
//        
        getStylesheets().add(getClass().getResource("ceditor.css").toExternalForm());

        internal = new ConfigurationEditorInternal(this);

        if (autoScroll) {
            this.sceneProperty().addListener((ov, o, n) -> initializeFocuseListening());
        }

        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setFitToWidth(true);
        this.setContent(internal);
    }

    public ConfigurationEditor(Configuration model, boolean readOnly, boolean autoScroll, Predicate<Property> filter) {
        this(autoScroll);
        setModel(model, readOnly, filter);
    }

    public void setModel(Configuration model, boolean readOnly, Predicate<Property> filter) {
        this.model = model;
        internal.setModel(model, readOnly, filter);
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
            selectNode(child, false);
        }
    }

    private void initializeFocuseListening() {
        if (getScene() == null) {
            return;
        }
        System.out.println("Initializing scene...");
        getScene().focusOwnerProperty().addListener((ov, o, n) -> selectNode(n, false));
    }

    public void select(TitledPane selected) {
        selectNode(selected, false);
    }

    private void selectNode(Node n, boolean expand) {

        if (selectedNode == n) {
            return;
        }

        if (FXUtils.lookupParent(n, node -> node == this) == null) {
            return;
        }

        TitledPane newSelection = (TitledPane) FXUtils.lookupParent(n, node -> node instanceof TitledPane, node -> node == this);

        //expand new selection: 
//        if (newSelection != null && expand) {
//            FXUtils.lookupParents(newSelection, node -> node instanceof TitledPane, node -> node == internal)
//                    .forEach(node -> {
//                        TitledPane tnode = (TitledPane) node;
//                        tnode.setExpanded(true);
//                    });
//        }

        if (selectedNode != newSelection) {
            if (newSelection != null && selectedNode != null) {
                if (selectedNode instanceof Selectable) {
                    ((Selectable) selectedNode).selectedProperty().set(false);
                }
                selectedNode.getStyleClass().remove("selected");
            }

            if (newSelection != null) {
                selectedNode = newSelection;
                if (newSelection instanceof Selectable) {
                    newSelection.setExpanded(true);
                    ((Selectable) newSelection).selectedProperty().set(true);
                }
                newSelection.getStyleClass().add("selected");
            }

        }

        if (newSelection != null) {
            FXUtils.ensureVisibility(this, newSelection, false);
        }

        if (n == internal) {
            FXUtils.ensureVisibility(this, n, false);
        }
    }

}
