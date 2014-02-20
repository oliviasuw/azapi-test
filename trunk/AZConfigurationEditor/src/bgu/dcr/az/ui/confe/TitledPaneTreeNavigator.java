/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.ui.util.FXUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Zovadi
 */
public class TitledPaneTreeNavigator extends BorderPane implements ListChangeListener<Node> {

    private final ScrollPane observable;
    private final TreeView navigationTree;

    private BiMap<TitledPane, TreeItem> treeNodes;

    public TitledPaneTreeNavigator(ScrollPane observable) {
        this.observable = observable;

        navigationTree = new TreeView();
        navigationTree.setShowRoot(false);
        navigationTree.setMinWidth(250);
        navigationTree.setPrefWidth(250);
        navigationTree.setMaxWidth(250);
        setLeft(navigationTree);
        setCenter(observable);

        treeNodes = HashBiMap.create();

        buildTree();

        navigationTree.getSelectionModel().selectedItemProperty().addListener((p, ov, nv) -> {
            if (nv != null && nv instanceof TreeItem) {
                TreeItem item = (TreeItem) nv;
                TitledPane tp = treeNodes.inverse().get(item);
                if (tp != null && tp instanceof Selectable) {
                    ((Selectable) tp).selectedProperty().set(true);
                }
            }
        });

        FXUtils.addChildListener(observable, this);
    }

    @Override
    public void onChanged(ListChangeListener.Change<? extends Node> change) {
        LinkedList<Node> add = new LinkedList<>();
        LinkedList<Node> remove = new LinkedList<>();
        while (change.next()) {
            if (change.wasAdded()) {
                LinkedList<Node> open = new LinkedList<>();
                for (Node item : change.getAddedSubList()) {
                    System.out.println("Added node: " + item);
                    open.add(item);
                    add.add(item);
                }
                fillTreeNodes(open);
            }
            if (change.wasRemoved()) {
                for (Node item : change.getRemoved()) {
                    for (Node child : FXUtils.lookupDirectChildren(item, n -> n instanceof TitledPane)) {
                        removeSubTree((TitledPane) child);
                    }
                    remove.add(item);
                }
            }
        }
        add.forEach(e -> FXUtils.addChildListener(e, this));
        remove.forEach(e -> FXUtils.removeChildListener(e, this));
    }

    private void buildTree() {
        System.out.println("rebuilding tree... ");
        treeNodes = HashBiMap.create();

        TreeItem root = new TreeItem();
        root.setExpanded(true);
        navigationTree.setRoot(root);
        LinkedList<Node> open = new LinkedList<>();
        open.add(observable);
        fillTreeNodes(open);
    }

    private void fillTreeNodes(LinkedList<Node> open) {
        while (!open.isEmpty()) {
            Node parent = open.remove();

            for (Node child : FXUtils.lookupDirectChildren(parent, n -> n instanceof TitledPane)) {
                addTreeItem((TitledPane) child);
                open.add(child);
            }
        }
    }

    private void addTreeItem(TitledPane childItem) {
        if (treeNodes.keySet().contains(childItem)) {
            return;
        }
        Node parentItem = FXUtils.lookupParent(childItem, n -> n != childItem && (n instanceof TitledPane || n == observable));
        TreeItem parent = navigationTree.getRoot();
        if (parentItem instanceof TitledPane) {
            parent = treeNodes.get((TitledPane) parentItem);
            if (parent == null) {
                addTreeItem((TitledPane) parentItem);
            }
            parent = treeNodes.get((TitledPane) parentItem);
        }

        TreeItem child = new TreeItem();
        child.expandedProperty().bindBidirectional(childItem.expandedProperty());
        child.graphicProperty().bind(childItem.graphicProperty());
        child.valueProperty().bind(childItem.textProperty());
        if (childItem instanceof Selectable) {
            ((Selectable) childItem).selectedProperty().addListener((p, ov, nv) -> {
                if (nv && treeNodes.containsKey(childItem)) {
                    TreeItem item = treeNodes.get(childItem);
                    navigationTree.getSelectionModel().select(item);
                }
            });
        }
        parent.getChildren().add(child);
        treeNodes.put(childItem, child);
        System.out.println("Added child: " + childItem);
    }

    private void removeSubTree(TitledPane root) {
        if (!treeNodes.keySet().contains(root)) {
            return;
        }

        removeChildren(root);

        TreeItem node = treeNodes.get(root);

        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }

        treeNodes.remove(root);
    }

    private void removeChildren(TitledPane root) {
        System.out.println("removing children " + root);

        if (!treeNodes.keySet().contains(root)) {
            return;
        }

        TreeItem rootNode = treeNodes.get(root);

        LinkedList<TreeItem> open = new LinkedList<>();
        open.add(rootNode);

        while (!open.isEmpty()) {
            TreeItem parentNode = open.remove();
            for (Object node : parentNode.getChildren().toArray()) {
                TreeItem item = (TreeItem) node;
                parentNode.getChildren().remove(item);
                treeNodes.inverse().remove(item);
                open.add(item);
            }
        }
    }

}
