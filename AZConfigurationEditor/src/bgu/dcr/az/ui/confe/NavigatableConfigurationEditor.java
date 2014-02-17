/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.impl.FromCollectionPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author Zovadi
 */
public class NavigatableConfigurationEditor extends HBox implements PropertyEditor {

    private final ConfigurationEditor configurationEditor;
    private final ConfigurationPropertyEditor configurationPropertyEditor;
    private final CollectionPropertyEditor collectionPropertyEditor;
    private final TerminalPropertyEditor terminalPropertyEditor;
    private final ScrollPane scrollPane;
    private final TreeView configurationTree;

    private Configuration configuration;
    private boolean readOnly;

    private Map<Object, TreeItem> treeNodes;

    public NavigatableConfigurationEditor() {
        configurationEditor = new ConfigurationEditor(this);
        configurationPropertyEditor = new ConfigurationPropertyEditor(this, false);
        collectionPropertyEditor = new CollectionPropertyEditor(this);
        terminalPropertyEditor = new TerminalPropertyEditor();

        configurationTree = new TreeView();
        configurationTree.setShowRoot(true);
        configurationTree.setMinWidth(250);
        configurationTree.setPrefWidth(250);
        configurationTree.setMaxWidth(250);
        configurationTree.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> navigateToSelectedItem());
        getChildren().add(configurationTree);

        scrollPane = new ScrollPane(configurationEditor);
        scrollPane.setMinWidth(500);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        getChildren().add(scrollPane);

        treeNodes = new HashMap<>();

        setStyle("-fx-border: 10px solid; -fx-border-color: red;");
    }

    private void navigateToSelectedItem() {
        Object item = configurationTree.getSelectionModel().getSelectedItem();
        if (item != null) {
            TreeItemProperty p = (TreeItemProperty) ((TreeItem) item).getValue();
            if (p.getConfiguration() != null) {
                configurationEditor.setModel(p.getConfiguration(), readOnly);
                scrollPane.setContent(configurationEditor);
            }
            if (p.getProperty() != null) {
                final Property property = p.getProperty();
                if (PropertyUtils.isPrimitive(property)) {
                    terminalPropertyEditor.setModel(p.getProperty(), readOnly);
                    scrollPane.setContent(terminalPropertyEditor);
                } else if (PropertyUtils.isCollection(property)) {
                    collectionPropertyEditor.setModel(property, readOnly);
                    scrollPane.setContent(collectionPropertyEditor);
                } else {
                    configurationPropertyEditor.setModel(property, readOnly);
                    scrollPane.setContent(configurationPropertyEditor);
                }
            }
        }
    }

    @Override

    public void setModel(Configuration configuration, boolean readOnly) {
        this.readOnly = readOnly;
        this.configuration = configuration;
        configurationEditor.setModel(configuration, readOnly);

        buildTree();
        scrollPane.setContent(configurationEditor);
    }

    @Override
    public void setModel(Property property, boolean readOnly) {
    }

    @Override
    public Property getModel() {
        return null;
    }

    private void buildTree() {
        treeNodes = new HashMap<>();

        TreeItem root = new TreeItem(new TreeItemProperty(configuration, configuration.registeredName()));
        root.setExpanded(true);
        configurationTree.setRoot(root);
        treeNodes.put(configuration, root);

        LinkedList open = new LinkedList();
        open.add(configuration);

        fillTreeNodes(open);
    }

    public void removeSubTree(Object root) {
        if (!treeNodes.keySet().contains(root)) {
            return;
        }
        TreeItem node = treeNodes.get(root);

        node.getParent().getChildren().remove(node);
    }

    public void removeChildren(Object root) {
        if (!treeNodes.keySet().contains(root)) {
            return;
        }

        TreeItem parentNode = treeNodes.get(root);

        for (Object k : treeNodes.keySet().toArray()) {
            TreeItem node = treeNodes.get(k);
            if (parentNode == node.getParent()) {
                parentNode.getChildren().remove(node);
                treeNodes.remove(k);
            }
        }
    }

    public void addFromConfigurationTreeNodes(Property property) {
        removeChildren(property);

        if (property.get() == null) {
            return;
        }

        LinkedList open = new LinkedList();

        addFromConfigurationTreeNodes(((FromConfigurationPropertyValue) property.get()).getValue(), property, open);

        fillTreeNodes(open);
    }

    public void addCollectionItemTreeNode(Property collection, Property item) {
        removeChildren(item);

        if (item.get() == null) {
            return;
        }

        LinkedList open = new LinkedList();

        if (item.get() instanceof FromConfigurationPropertyValue) {
            addLeafTreeNode(collection, item, "item", open);
            addFromConfigurationTreeNodes(((FromConfigurationPropertyValue) item.get()).getValue(), item, open);
        }

        if (item.get() instanceof FromCollectionPropertyValue) {
            addLeafTreeNode(collection, item, "item", open);
        }

        if (item.get() instanceof FromStringPropertyValue) {
            addLeafTreeNode(collection, item, "item", open);
        }

        fillTreeNodes(open);
    }

    private void fillTreeNodes(LinkedList open) {
        while (!open.isEmpty()) {
            Object parent = open.remove();

            if (parent instanceof Configuration) {
                addFromConfigurationTreeNodes((Configuration) parent, parent, open);
            } else {
                Property prop = (Property) parent;
                if (prop.get() == null) {
                    continue;
                }
                if (prop.get() instanceof FromConfigurationPropertyValue) {
                    addFromConfigurationTreeNodes(((FromConfigurationPropertyValue) prop.get()).getValue(), parent, open);
                }
                if (prop.get() instanceof FromCollectionPropertyValue) {
                    addLeafTreeNode(parent, prop, "Collection of " + prop.name(), open);
                }
                if (prop.get() instanceof FromStringPropertyValue) {
                    addLeafTreeNode(parent, prop, prop.name(), open);
                }
            }
        }
    }

    private void addFromConfigurationTreeNodes(Configuration conf, Object parent, LinkedList open) {
        for (Property property : conf.properties()) {
            if (PropertyUtils.isPrimitive(property)) {
                addLeafTreeNode(parent, property, property.name(), open);
            } else if (PropertyUtils.isCollection(property)) {
                addLeafTreeNode(parent, property, "Collection of " + property.name(), open);
            } else {
                addConfigurationTreeNode(parent, property, property.name(), open);
            }
        }
    }

    private void addLeafTreeNode(Object parent, Property child, String treeName, LinkedList open) {
        TreeItem item = new TreeItem(new TreeItemProperty(child, treeName));
        item.setExpanded(true);
        treeNodes.get(parent).getChildren().add(item);
        treeNodes.put(child, item);
    }

    private void addConfigurationTreeNode(Object parent, Property child, String treeName, LinkedList open) {
        TreeItem item = new TreeItem(new TreeItemProperty(child, treeName));
        item.setExpanded(true);
        treeNodes.get(parent).getChildren().add(item);
        treeNodes.put(child, item);
        open.add(child);
    }

    private static class TreeItemProperty {

        private final Configuration configuration;
        private final Property property;
        private final String name;

        public TreeItemProperty(Configuration configuration, String name) {
            this.configuration = configuration;
            this.property = null;
            this.name = name;
        }

        public TreeItemProperty(Property property, String name) {
            this.configuration = null;
            this.property = property;
            this.name = name;
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public Property getProperty() {
            return property;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
