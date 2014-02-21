/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.ui.util.FXUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.LinkedList;
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

    private BiMap<TitledPane, TreeItem> titledPaneToTreeItem;
    private BiMap<TitledPane, TitledPane> parentToChildTitledPane;

    public TitledPaneTreeNavigator(ScrollPane observable) {
        this.observable = observable;

        navigationTree = new TreeView();
        navigationTree.setShowRoot(false);
        navigationTree.setMinWidth(250);
        navigationTree.setPrefWidth(250);
        navigationTree.setMaxWidth(250);
        setLeft(navigationTree);
        setCenter(observable);

        titledPaneToTreeItem = HashBiMap.create();
        parentToChildTitledPane = HashBiMap.create();

        buildTree();

        navigationTree.getSelectionModel().selectedItemProperty().addListener((p, ov, nv) -> {
            if (nv != null && nv instanceof TreeItem) {
                TreeItem item = (TreeItem) nv;
                TitledPane tp = titledPaneToTreeItem.inverse().get(item);
                if (tp != null && tp instanceof Selectable) {
                    ((Selectable) tp).selectedProperty().set(true);
                }
            }
        });

        FXUtils.addChildListener(observable, this);
    }

    @Override
    public void onChanged(ListChangeListener.Change<? extends Node> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                LinkedList<Node> open = new LinkedList<>();
                for (Node item : change.getAddedSubList()) {
                    open.add(item);
                }
                fillTreeNodes(open);
            }
            if (change.wasRemoved()) {
                for (Node item : change.getRemoved()) {
                    for (Node child : FXUtils.lookupDirectChildren(item, n -> n instanceof TitledPane)) {
                        removeSubTree((TitledPane) child);
                    }
                }
            }
        }
    }

    private void buildTree() {
        System.out.println("rebuilding tree... ");
        titledPaneToTreeItem = HashBiMap.create();
        parentToChildTitledPane = HashBiMap.create();

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
            
            if (parent instanceof TitledPane) {
                addTreeItem((TitledPane)parent);
            }

            for (Node child : FXUtils.lookupDirectChildren(parent, n -> n instanceof TitledPane)) {
                if (parent instanceof TitledPane) {
                    parentToChildTitledPane.put((TitledPane) parent, (TitledPane) child);
                }
                addTreeItem((TitledPane) child);
                open.add(child);
            }
        }
    }

    private void addTreeItem(TitledPane childItem) {
        if (titledPaneToTreeItem.keySet().contains(childItem)) {
            return;
        }
        TreeItem parent = findParent(childItem);

        TreeItem child = new TreeItem();
        child.expandedProperty().bindBidirectional(childItem.expandedProperty());
        child.graphicProperty().bind(childItem.graphicProperty());
        child.valueProperty().bind(childItem.textProperty());
        if (childItem instanceof Selectable) {
            ((Selectable) childItem).selectedProperty().addListener((p, ov, nv) -> {
                if (nv && titledPaneToTreeItem.containsKey(childItem)) {
                    TreeItem item = titledPaneToTreeItem.get(childItem);
                    navigationTree.getSelectionModel().select(item);
                }
            });
        }
        parent.getChildren().add(child);
        titledPaneToTreeItem.put(childItem, child);
        FXUtils.addChildListener(childItem, this);
    }

    private TreeItem findParent(TitledPane childItem) {
        for (TitledPane parent : titledPaneToTreeItem.keySet()) {
            if (FXUtils.lookupDirectChildren(parent, n -> n instanceof TitledPane).contains(childItem)) {
                return titledPaneToTreeItem.get(parent);
            }
        }
        return navigationTree.getRoot();
    }

    private void removeSubTree(TitledPane root) {
        if (!titledPaneToTreeItem.keySet().contains(root)) {
            return;
        }
        FXUtils.removeChildListener(root, this);

        removeChildren(root);

        TreeItem node = titledPaneToTreeItem.get(root);

        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }

        titledPaneToTreeItem.remove(root);
    }

    private void removeChildren(TitledPane root) {
        if (!titledPaneToTreeItem.keySet().contains(root)) {
            return;
        }

        TreeItem rootNode = titledPaneToTreeItem.get(root);

        LinkedList<TreeItem> open = new LinkedList<>();
        open.add(rootNode);

        while (!open.isEmpty()) {
            TreeItem parentNode = open.remove();
            for (Object node : parentNode.getChildren().toArray()) {
                TreeItem item = (TreeItem) node;
                FXUtils.removeChildListener(titledPaneToTreeItem.inverse().get(item), this);
                parentNode.getChildren().remove(item);
                titledPaneToTreeItem.inverse().remove(item);
                open.add(item);
            }
        }
    }

}
