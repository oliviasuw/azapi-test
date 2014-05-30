/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.containers;

import bgu.dcr.az.mui.View;
import bgu.dcr.az.mui.ViewContainer;
import javafx.scene.control.TabPane;

/**
 *
 * @author bennyl
 */
public class TabContainer extends TabPane implements View {

    private String prefix = null;
    private ViewContainer container;

    public TabContainer() {
    }

    public TabContainer(String prefix) {
        this.prefix = prefix;
    }

    public void setViewsPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void bind(ViewContainer container) {
        this.container = new ViewContainer(container);
        updatePrefix();
    }

    private void updatePrefix() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
