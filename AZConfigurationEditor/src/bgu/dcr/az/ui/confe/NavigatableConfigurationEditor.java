/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.conf.impl.ConfigurableTypeInfoImpl;
import bgu.dcr.az.anop.conf.impl.FromCollectionPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.anop.conf.impl.PropertyImpl;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.PropertyUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Zovadi
 */
public class NavigatableConfigurationEditor extends BorderPane implements PropertyEditor {

    private final ConfigurationEditor configurationEditor;
    private final TreeView configurationTree;

    private Configuration configuration;
    private boolean updateSelectionAllowed = true;

    private Map<Object, TreeItem> treeNodes;

    public NavigatableConfigurationEditor() {

        configurationTree = new TreeView();
        configurationTree.setShowRoot(true);
        configurationTree.setMinWidth(250);
        configurationTree.setPrefWidth(250);
        configurationTree.setMaxWidth(250);
        configurationTree.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> {
            if (updateSelectionAllowed) {
                updateSelectionAllowed = false;
                navigateToSelectedItem();
                updateSelectionAllowed = true;
            }
        });
        setLeft(configurationTree);

//        scrollPane.setStyle("-fx-border: 10px solid; -fx-border-color: red;");
        configurationEditor = new ConfigurationEditor();
        configurationEditor.getListeners().add(new ConfigurationEditor.ConfigurationEditorListener() {
            @Override
            public void onPropertyValueChanged(ConfigurationEditor source, Property property) {
                if (property != null) {
                    removeChildren(property);
                    if (property.get() != null) {
                        if (property.get() instanceof FromConfigurationPropertyValue) {
                            addFromConfigurationTreeNodes(property);
                        }
                        for (TreeItem item : treeNodes.values()) {
                            Object temp = item.getValue();
                            item.setValue(null);
                            item.setValue(temp);
                        }
                    }
                }
            }

            @Override

            public void onPropertyValueAdded(ConfigurationEditor source, Property collection, PropertyValue value) {
                if (collection != null && value != null) {
                    addCollectionItemTreeNode(collection, value);
                }
            }

            @Override
            public void onPropertyValueRemoved(ConfigurationEditor source, Property collection, PropertyValue value) {
                if (collection != null && value != null) {
                    removeSubTree(ValueComparablePseudoProperty.fromCollectionItem(collection, value));
                }
            }

            @Override
            public void onItemSelecttion(ConfigurationEditor source, Object item) {
                if (item == null) {
                    return;
                }
                TreeItem node = null;
                if (item instanceof Property) {
                    node = treeNodes.get(item);
                } else if (item instanceof PropertyValue) {
                    node = treeNodes.get(ValueComparablePseudoProperty.fromCollectionItem(null, (PropertyValue) item));
                }

                if (node != null) {
                    configurationTree.getSelectionModel().select(node);
                }
            }
        });
        setCenter(configurationEditor);

        treeNodes = new HashMap<>();
//        setStyle("-fx-border: 10px solid; -fx-border-color: red;");
    }

    private void navigateToSelectedItem() {
        TreeItem item = (TreeItem) configurationTree.getSelectionModel().getSelectedItem();
        System.out.println("navigating to " + item);
        if (item != null) {
            Object value = ((TreeItemProperty) item.getValue()).getItem();
            configurationEditor.select(value instanceof ValueComparablePseudoProperty ? ((ValueComparablePseudoProperty) value).get() : value);
        }
    }

    @Override

    public void setModel(Configuration configuration, boolean readOnly) {
        this.configuration = configuration;
        buildTree();
        configurationEditor.setModel(configuration, readOnly);
    }

    @Override
    public void setModel(Property property, boolean readOnly) {
    }

    @Override
    public Property getModel() {
        return null;
    }

    private void buildTree() {
        System.out.println("rebuilding tree... ");
        treeNodes = new HashMap<>();

        TreeItem root = new TreeItem(new TreeItemProperty(configuration, configuration.registeredName()));
        root.setExpanded(true);
        configurationTree.setRoot(root);
        treeNodes.put(configuration, root);

        LinkedList open = new LinkedList();
        open.add(configuration);

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
                    addFromCollectionTreeNode(prop, open);
                }
                if (prop.get() instanceof FromStringPropertyValue) {
//                    addLeafTreeNode(parent, prop, prop.name());
                }
            }
        }
    }

    private void addFromConfigurationTreeNodes(Configuration conf, Object parent, LinkedList open) {
        for (Property property : conf.properties()) {
            if (!PropertyUtils.isPrimitive(property) && !PropertyUtils.isCollection(property)) {
                addInternalTreeNode(parent, property, property.name(), open);
            }
        }
        for (Property property : conf.properties()) {
            if (PropertyUtils.isCollection(property)) {
                addInternalTreeNode(parent, property, "Collection of " + property.name(), open);
            }
        }
    }

    private void addFromCollectionTreeNode(Property parent, LinkedList open) {
        addFromCollectionTreeNode(parent, (FromCollectionPropertyValue) parent.get(), open);
    }

    private void addFromCollectionTreeNode(Property parent, Iterable<PropertyValue> values, LinkedList open) {
        for (PropertyValue item : values) {
            Property pseudoProperty = ValueComparablePseudoProperty.fromCollectionItem(parent, item);
            if (PropertyUtils.isPrimitive(pseudoProperty)) {

            } else if (PropertyUtils.isCollection(pseudoProperty)) {
                addInternalTreeNode(parent, pseudoProperty, "item", open);
            } else {
                addInternalTreeNode(parent, pseudoProperty, "item", open);
            }
        }
    }

    private void addLeafTreeNode(Object parent, Property child, String treeName) {
        if (child.doc() == null || !"false".equals(child.doc().first("UIVisibility"))) {
            TreeItem item = new TreeItem(new TreeItemProperty(child, treeName));
            item.setExpanded(true);
            treeNodes.get(parent).getChildren().add(item);
            treeNodes.put(child, item);
            Label infoContainer = new Label("");
            PropertyEditor.updateInfo(infoContainer, child);
            item.setGraphic(infoContainer);
        }
    }

    private void addInternalTreeNode(Object parent, Property child, String treeName, LinkedList open) {
        addLeafTreeNode(parent, child, treeName);
        open.add(child);

    }

    private static class TreeItemProperty {

        private final Object item;
        private final String name;

        public TreeItemProperty(Object item, String name) {
            this.item = item;
            this.name = name;
        }

        public Object getItem() {
            return item;
        }

        @Override
        public String toString() {
            if (item instanceof ValueComparablePseudoProperty) {
                ValueComparablePseudoProperty pv = (ValueComparablePseudoProperty)item;
                return RegisteryUtils.getRegistery().getRegisteredClassName(pv.typeInfo().getType()) + subNameSelector(pv);
            } else if (item instanceof Property) {
                return name + subNameSelector((Property) item);
            } else {
                return name;
            }
        }

        private String subNameSelector(Property property) {
            if (property != null && property.get() != null && property.get() instanceof FromConfigurationPropertyValue) {
                FromConfigurationPropertyValue fcpv = (FromConfigurationPropertyValue) property.get();
                Property confName = fcpv.getValue().get("name") == null ? fcpv.getValue().get("Name") : fcpv.getValue().get("name");
                if (confName != null && confName.get() != null) {
                    return " [" + confName.get().stringValue() + "]";
                }
            }
            return "";
        }
    }

    public void removeSubTree(Property root) {
        if (!treeNodes.keySet().contains(root)) {
            return;
        }

        doWhilePreserveSelection(() -> {
            removeChildren(root);

            TreeItem node = treeNodes.get(root);

            if (node.getParent() != null) {
                node.getParent().getChildren().remove(node);
            }

            treeNodes.remove(root);
        });
    }

    private void doWhilePreserveSelection(Runnable r) {
        TreeItem oldSelection = (TreeItem) configurationTree.getSelectionModel().getSelectedItem();

        r.run();

        System.out.println("going back to old selection: " + oldSelection);

        boolean old = updateSelectionAllowed;
        updateSelectionAllowed = false;

        configurationTree.getSelectionModel().select(oldSelection);
        if (oldSelection != null) {
            final int row = configurationTree.getRow(oldSelection);
            System.out.println("and focusing: " + row);
            configurationTree.getFocusModel().focus(row + 1);
        }

        updateSelectionAllowed = old;

    }

    public void removeChildren(Property root) {
        System.out.println("removing children " + root);

        if (!treeNodes.keySet().contains(root)) {
            return;
        }

        TreeItem rootNode = treeNodes.get(root);

        doWhilePreserveSelection(() -> {

            LinkedList open = new LinkedList();
            open.add(rootNode);

            while (!open.isEmpty()) {
                TreeItem parentNode = (TreeItem) open.remove();
                for (Object node : parentNode.getChildren().toArray()) {
                    parentNode.getChildren().remove(node);
                    treeNodes.remove(((TreeItemProperty) ((TreeItem) node).getValue()).getItem());
                    open.add(node);
                }
            }
        });

    }

    private void addFromConfigurationTreeNodes(Property property) {
        if (property == null || property.get() == null) {
            return;
        }
        LinkedList open = new LinkedList();

        addFromConfigurationTreeNodes(((FromConfigurationPropertyValue) property.get()).getValue(), property, open);

        fillTreeNodes(open);
    }

    public void addCollectionItemTreeNode(Property collection, PropertyValue item) {
        if (item == null || collection == null) {
            return;
        }

        LinkedList open = new LinkedList();

        addFromCollectionTreeNode(collection, Arrays.asList(item), open);
        fillTreeNodes(open);

    }

    private static class ValueComparablePseudoProperty extends PropertyImpl {

        public static Property fromCollectionItem(Property collection, PropertyValue item) {
            Class type = collection == null ? null : collection.typeInfo().getGenericParameters().get(0).getType();
            ValueComparablePseudoProperty pseudoProperty = new ValueComparablePseudoProperty("item", type);
            pseudoProperty.set(item);
            return pseudoProperty;
        }

        public ValueComparablePseudoProperty(String name, Class type) {
            super(name, null, new ConfigurableTypeInfoImpl(type), JavaDocParser.parse(""));
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValueComparablePseudoProperty && ((ValueComparablePseudoProperty) obj).get() == get();
        }

        @Override
        public String toString() {
            return "ValueComparablePseudoProperty{" + '}';
        }

        @Override
        public int hashCode() {
            return get() == null ? 7 : get().hashCode();
        }
    }

}
