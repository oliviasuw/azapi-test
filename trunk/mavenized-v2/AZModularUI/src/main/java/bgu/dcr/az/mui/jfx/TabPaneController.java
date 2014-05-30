/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.jfx;

import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerAttributes;
import bgu.dcr.az.mui.ControllerRegistery;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author bennyl
 */
public class TabPaneController extends Controller<TabPane> {

    private TabPane view;
    private String subcontrollersPrefix;

    public TabPaneController(Controller owner, TabPane view, String subcontrollersPrefix) {
        this.view = view;
        this.subcontrollersPrefix = subcontrollersPrefix;
        owner.manage(this);
    }

    public void setSubcontrollersPrefix(String subcontrollersPrefix) {
        this.subcontrollersPrefix = subcontrollersPrefix;
    }

    @Override
    public TabPane getView() {
        return view;
    }

    @Override
    public void onLoadView() {
        
        if (view == null) {
            throw new UnsupportedOperationException("tab pane controller must be supplied with a tab pane");
        }
        
        manageAll(ControllerRegistery.get().createControllers(subcontrollersPrefix, this));
        for (Controller g : this) {
            ControllerAttributes attr = ControllerRegistery.get().getAttributes(g);

            Tab t = new Tab(attr.title());
            g.onLoadView();
            t.setContent((Node) g.getView());

            view.getTabs().add(t);
        }
    }

}
