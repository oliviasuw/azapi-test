/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

/**
 * a controller that dont have a view attached to it - it is only responsible to
 * manage resources (modules) and other controllers
 *
 * @author bennyl
 */
public class RootController extends Controller{

    @Override
    public Object getView() {
        throw new UnsupportedOperationException("Not supported - root controller does not contain a view");
    }

    @Override
    protected void onLoadView() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }


}
