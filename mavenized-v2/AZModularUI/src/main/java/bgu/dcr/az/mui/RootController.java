/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.conf.modules.info.SimpleInfoStream;

/**
 * a controller that dont have a view attached to it - it is only responsible to
 * manage resources (modules) and other controllers
 *
 * @author bennyl
 */
public class RootController extends BaseController<Void> {

    InfoStream istream;

    public RootController() {
        install(InfoStream.class, istream = new SimpleInfoStream());
    }

    public InfoStream infoStream() {
        return istream;
    }

    @Override
    public Void _getView() {
        throw new UnsupportedOperationException("Not supported - root controller does not contain a view");
    }

    @Override
    protected void onLoadView() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
