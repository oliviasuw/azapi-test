/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.info.InfoStream;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class RemovalTrackingInfoStream implements InfoStream {

    private Set removalKeys = new HashSet();
    private InfoStream wrapee;

    public RemovalTrackingInfoStream(InfoStream wrapee) {
        this.wrapee = wrapee;
    }

    @Override
    public <T> void listen(Class<T> channel, Object removalKey, InfoStreamListener<T> listener) {
        removalKeys.add(removalKey);
        wrapee.listen(channel, removalKey, listener);
    }

    @Override
    public void removeListeners(Object removalKey) {
        removalKeys.remove(removalKey);
        wrapee.removeListeners(removalKey);
    }

    @Override
    public void write(Object data, Class... channels) {
        wrapee.write(data, channels);
    }

    @Override
    public boolean hasListeners(Class channel) {
        return wrapee.hasListeners(channel);
    }

    public void removeAllListeners() {
        for (Object r : removalKeys) {
            wrapee.removeListeners(r);
        }

        removalKeys.clear();
    }

}
