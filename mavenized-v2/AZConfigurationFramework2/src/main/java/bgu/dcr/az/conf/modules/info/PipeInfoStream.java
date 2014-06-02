/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

/**
 *
 * @author bennyl
 */
public class PipeInfoStream implements InfoStream {

    private SimpleInfoStream current;
    private InfoStream target = null;

    public PipeInfoStream(InfoStream target) {
        current = new SimpleInfoStream();
        this.target = target;
    }

    public PipeInfoStream() {
        this(null);
    }

    public void setPipeTarget(InfoStream target) {
        this.target = target;
    }

    @Override
    public void write(Object data, Class... recepients) {
        current.write(data);
        if (target != null) {
            target.write(data);
        }
    }

    @Override
    public boolean hasListeners(Class dataType) {
        return current.hasListeners(dataType) || target.hasListeners(dataType);
    }

    @Override
    public <T> void listen(Class<T> channel, Object removalKey, InfoStreamListener<T> listener) {
        current.listen(channel, removalKey, listener);
    }

    @Override
    public void removeListeners(Object removalKey) {
        current.removeListeners(removalKey);
    }

}
