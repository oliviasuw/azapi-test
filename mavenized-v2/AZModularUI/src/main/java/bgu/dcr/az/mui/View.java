/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.Module;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author bennyl
 */
public interface View extends Initializable, Module<ViewContainer>{

    default void initialize(URL location, ResourceBundle resources) {
    }
    
    void bind(ViewContainer container);
    
    default void onShow(){};
    
    default void onHide(){};
    
    default void onLoad(){};
    
    default void onClose(){};
}
