/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.modules.info.InfoStream;

/**
 *
 * @author bennyl
 */
public abstract class Controller<T> extends BaseController<T> {

    private RemovalTrackingInfoStream istream;

    public InfoStream infoStream() {
        if (istream == null) {
            throw new UnsupportedOperationException("cannot provide infostream before beeing attached to a view controller");
        }
        return istream;
    }

    void onRemovedFromParent() {
        istream.removeAllListeners();
    }

    @Override
    public void installInto(ModuleContainer mc) {
        super.installInto(mc);
        if (mc instanceof BaseController) {
            install(InfoStream.class, istream = new RemovalTrackingInfoStream(mc.require(InfoStream.class)));
        } else {
            throw new UnsupportedOperationException("controllers can only reside inside other controllers");
        }
    }

}
