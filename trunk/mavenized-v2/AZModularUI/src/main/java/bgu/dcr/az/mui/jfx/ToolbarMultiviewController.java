/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.jfx;

import bgu.dcr.az.common.collections.IterableUtils;
import bgu.dcr.az.mui.Controller;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.SegmentedButton;

/**
 *
 * @author bennyl
 */
public class ToolbarMultiviewController extends Controller<BorderPane> {

    private BorderPane view;
    private String viewsGroup;

    public ToolbarMultiviewController(String viewsGroup) {
        this.viewsGroup = viewsGroup;
    }

    @Override
    public BorderPane _getView() {
        return view;
    }

    @Override
    protected void onLoadView() {
        view = new BorderPane();
        view.getStyleClass().add("toolbar-multiview");
        
        ToolBar toolbar = new ToolBar();
        confToolbar(toolbar);
        SegmentedButton toolbarViews = new SegmentedButton();

        BorderPane content = new BorderPane();

        BorderPane toolbarHolder = new BorderPane();
        toolbarHolder.getStyleClass().add("toolbar-holder");

        toolbarHolder.setLeft(toolbar);
        view.setTop(toolbarHolder);
        Iterable<Controller> all = findAndInstallAll(viewsGroup);
        all = IterableUtils.sorted(all, (a,b) -> {
            String sa = a.attributes().get("index");
            String sb = b.attributes().get("index");
            
            if (sa == null) return -1;
            if (sb == null) return 1;
            return sa.compareTo(sb);
        });
        
        Controller[] selected = {null};
        
        for (Controller c : all) {
            String title = c.attributes().title();
            if (title == null) {
                title = "untitled";
            }

            final ToggleButton viewSelector = new ToggleButton(title);
            viewSelector.setOnAction(e -> {
                
                content.getChildren().clear();
                if (selected[0] != null) selected[0].onHide();
                selected[0] = c;
                
                final Object v = c.getView();
                content.setCenter((Node) v);

                if (c instanceof WithSubToolbar) {
                    final ToolBar sideToolbar = ((WithSubToolbar) c).getToolbar();
                    toolbarHolder.setCenter(sideToolbar);
                    confToolbar(sideToolbar);
                    
                } else {
                    toolbarHolder.setCenter(null);
                }
                
                c.onShow();
            });

            toolbarViews.getButtons().add(viewSelector);
        }

        toolbar.getItems().add(toolbarViews);
        view.setCenter(content);
    }

    private void confToolbar(final ToolBar sideToolbar) {
        sideToolbar.setPrefHeight(35);
        sideToolbar.setMaxHeight(35);
        sideToolbar.setMinHeight(35);
    }

    public static interface WithSubToolbar {

        ToolBar getToolbar();
    }

}
