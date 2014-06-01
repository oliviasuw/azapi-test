/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules.info;

import bgu.dcr.az.conf.modules.Module;
import java.util.function.Supplier;

/**
 *
 * this class is similar to the google's eventbus concept but much simpler, it
 * allows for registering to information events (also called channels) and
 * receiving these events when they are written into the stream.
 *
 * it is mostly used as a communication channel between modules
 *
 * @author User
 */
public interface InfoStream extends Module {

    /**
     * start listening for the given eventType, the given removal key can be
     * used in the method {@link #removeListeners(java.lang.Object) }
     * in order to remove all the listeners that was registered with the same
     * removal key.
     *
     *
     * @param <T> the type of the event to listen on
     * @param channel the class representing the type of the event to listen on
     * @param removalKey a removal key to be used later in the
     * {@link #removeListeners(java.lang.Object)} method
     * @param listener
     *
     * @throws NullPointerException if any of the removalKey or the eventType
     * are null.
     */
    <T> void listen(Class<T> channel, Object removalKey, InfoStreamListener<T> listener);

    /**
     * same as
     * {@link #listen(java.lang.Class, java.lang.Object, bgu.dcr.az.conf.modules.info.InfoStream.InfoStreamListener)}
     * but the removalKey used is the listener itself
     *
     * @param <T>
     * @param channel
     * @param listener
     */
    default <T> void listen(Class<T> channel, InfoStreamListener<T> listener) {
        listen(channel, listener, listener);
    }

    /**
     * remove all the listeners that was registered using the given removalKey
     *
     * @param removalKey
     */
    void removeListeners(Object removalKey);

    /**
     * write the following object to the stream - will notify all the listeners
     * that are listening on any of the given channels
     *
     * @param data
     * @param channels
     */
    void write(Object data, Class... channels);

    /**
     * same as {@link #write(java.lang.Object, java.lang.Class...)} but the
     * given channels are data.getClass();
     *
     * @param data
     */
    default void write(Object data) {
        write(data, data.getClass());
    }

    /**
     * only if there are listeners on any of the given channels then the given
     * supplier will be called and the event will be writen to all the channels
     *
     * this method should be used when creating the event may have negative
     * effect on performance and will be consider a waste if none are listening
     * for it
     *
     * @param data
     * @param channels
     */
    default void writeIfListening(Supplier data, Class... channels) {
        Object resolved = null;
        for (Class r : channels) {
            if (hasListeners(r)) {
                if (resolved == null) {
                    resolved = data.get();
                }

                write(resolved, r);
            }
        }
    }

    /**
     *
     * @param channel
     * @return true if there are any listeners on the given channel
     */
    boolean hasListeners(Class channel);

    @FunctionalInterface
    public static interface InfoStreamListener<T> {

        void info(T data);
    }
}
