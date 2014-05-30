/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.Module;
import java.util.function.Supplier;

/**
 *
 * @author User
 */
public interface InfoStream extends Module {

    <T> void listen(Class<T> dataType, InfoStreamListener<T> listener);

    void write(Object data, Class... recepients);

    default void write(Object data) {
        write(data, data.getClass());
    }

    default void writeIfListening(Supplier data, Class... recepients) {
        Object resolved = null;
        for (Class r : recepients) {
            if (hasListeners(r)) {
                if (resolved == null) {
                    resolved = data.get();
                }

                write(resolved, r);
            }
        }
    }

    boolean hasListeners(Class dataType);

    @FunctionalInterface
    public static interface InfoStreamListener<T> {

        void info(T data);
    }
}
