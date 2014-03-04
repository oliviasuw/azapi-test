/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.stat;

/**
 *
 * @author User
 */
public interface InfoStream {

    <T> void listen(Class<T> dataType, InfoStreamListener<T> listener);

    void write(Object data);

    boolean hasListeners(Class dataType);

    public static interface InfoStreamListener<T> {

        void info(T data);
    }
}
