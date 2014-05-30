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

    public PipeInfoStream() {
        current = new SimpleInfoStream();
    }

    public void setPipeTarget(InfoStream target) {
        this.target = target;
    }

    @Override
    public <T> void listen(Class<T> dataType, InfoStreamListener<T> listener) {
        current.listen(dataType, listener);
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

}
