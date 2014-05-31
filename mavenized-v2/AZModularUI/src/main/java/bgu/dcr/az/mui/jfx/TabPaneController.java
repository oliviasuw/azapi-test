/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.jfx;

import bgu.dcr.az.conf.modules.Module;
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
    private String subcontrollersGroup;

    public TabPaneController(Controller owner, TabPane view, String subcontrollersPrefix) {
        this.view = view;
        this.subcontrollersGroup = subcontrollersPrefix;
        owner.manage(this);
    }

    public void setSubcontrollersPrefix(String subcontrollersPrefix) {
        this.subcontrollersGroup = subcontrollersPrefix;
    }

    @Override
    public TabPane getView() {
        return view;
    }

    @Override
    public void supply(Class<? extends Module> moduleKey, Module module, boolean immidiateInit) {
        super.supply(moduleKey, module, immidiateInit); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    public void onLoadView() {

        if (view == null) {
            throw new UnsupportedOperationException("tab pane controller must be supplied with a tab pane");
        }
        
        System.out.println("manages: " + amountManaged());

        findAndManageAll(subcontrollersGroup);
        
        System.out.println("manages: " + amountManaged());
        
        for (Controller g : managedControllers()) {
            System.out.println("check " + g.getClass().getSimpleName());
            ControllerAttributes attr = ControllerRegistery.get().getAttributes(g);

            Tab t = new Tab(attr.title());
            g.loadView();
            t.setContent((Node) g.getView());

            view.getTabs().add(t);
        }
    }

}
