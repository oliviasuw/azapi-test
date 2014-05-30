/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.containers;

import bgu.dcr.az.mui.View;
import bgu.dcr.az.mui.ViewContainer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TabPane;

/**
 *
 * @author bennyl
 */
public class TabContainer extends TabPane implements View {

    private String prefix = null;
    private ViewContainer container;
    private String tokens = "";

    public TabContainer() {
    }

    public TabContainer(String prefix) {
        this.prefix = prefix;
    }

    public void setViewsPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * set of comma separated tokens (e.g., token1, token2, ..., tokenN)
     *
     * @param tokens
     */
    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    @Override
    public void onJoined(ViewContainer container) {
        this.container = new ViewContainer(container, tokens.split("\\s*,\\s*"));
        //search for views that are willing to join
    }

}
