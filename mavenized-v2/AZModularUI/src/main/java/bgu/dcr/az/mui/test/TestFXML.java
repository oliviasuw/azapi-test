/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.test;

import bgu.dcr.az.mui.RegisterView;
import bgu.dcr.az.mui.View;
import bgu.dcr.az.mui.ViewContainer;

/**
 * FXML Controller class
 *
 * @author bennyl
 */
@RegisterView("test.fxml.bla")
public class TestFXML implements View {

    public static boolean accept(ViewContainer vc) {
        return false;
    }

    @Override
    public void bind(ViewContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
