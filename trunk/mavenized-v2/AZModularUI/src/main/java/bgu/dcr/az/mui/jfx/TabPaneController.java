/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.jfx;

import bgu.dcr.az.common.collections.IterableUtils;
import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerAttributes;
import bgu.dcr.az.mui.ControllerRegistery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * tab pane controller - controlls a given tab pane, upon loading will fill it
 * will accepting controllers of the group set by
 * {@link #setSubcontrollersPrefix(java.lang.String)}, it supports tabs
 * ordering: if the accepting controllers defined {@code @tabIndex} tag in their
 * javadoc it will be compared lexicographically.
 *
 * @author bennyl
 */
public class TabPaneController extends Controller<TabPane> {

    private TabPane view;
    private String subcontrollersGroup;

    public TabPaneController(Controller owner, TabPane view, String subcontrollersPrefix) {
        this.view = view;
        this.subcontrollersGroup = subcontrollersPrefix;
        owner.install(this);
    }

    public void setSubcontrollersPrefix(String subcontrollersPrefix) {
        this.subcontrollersGroup = subcontrollersPrefix;
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

        view.getTabs().clear();

        findAndInstallAll(subcontrollersGroup);

        List<Controller> managed = IterableUtils.toList(installedControllers(), new ArrayList());
        Collections.sort(managed, (a, b) -> {
            String pa = ControllerRegistery.get().getAttributes(a).attr("tabIndex");
            String pb = ControllerRegistery.get().getAttributes(b).attr("tabIndex");

            if (pa == null) {
                return 1;
            }
            if (pb == null) {
                return -1;
            }
            return pa.compareTo(pb);
        });

        for (Controller g : managed) {
            ControllerAttributes attr = ControllerRegistery.get().getAttributes(g);

            String title = attr.title();
            if (title == null) {
                throw new UnsupportedOperationException("controllers accepting to be managed by tabpaneController must provide @title tag in their javadoc, " + managed.getClass().getSimpleName() + " dident.");
            }

            Tab t = new Tab(title);
            g.loadView();
            t.setContent((Node) g.getView());

            view.getTabs().add(t);
        }
    }

}
