/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.ui.components.onoffswitch.IconSwitch;
import bgu.dcr.az.ui.components.onoffswitch.OnOffSwitch;
import bgu.dcr.az.ui.components.onoffswitch.OnOffSwitchBuilder;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author User
 */
public class BasicStatisticsScreenCtl implements Initializable {

    @FXML
    HBox header;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final OnOffSwitch onOffSwitch = new OnOffSwitch();
//        onOffSwitch.set
        // TODO
        
        header.getChildren().add(onOffSwitch);
    }    
    
}
