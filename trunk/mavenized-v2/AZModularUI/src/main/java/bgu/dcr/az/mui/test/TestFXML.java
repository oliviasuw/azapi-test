/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.test;

import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;

/**
 * FXML Controller class
 *
 * @title test
 */
public class TestFXML extends FXMLController {

    @Override
    protected void onLoadView() {
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("bgu.dcr.az.algo.autogen.bgu_dcr_az_mui_app_SBB");
    }

}
