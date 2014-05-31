/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.main;

import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.jfx.TabPaneController;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author bennyl
 */
@RegisterController("main")
public class MainPage extends FXMLController {

    @FXML
    TabPane tabs;
    TabPaneController tabsController;

    @Override
    public void onLoadView() {
        tabsController = new TabPaneController(this, tabs, "main.pages");
    }
    
    public static boolean accept(Controller parent){
        return true;
    }

}
