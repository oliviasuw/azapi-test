/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

import java.util.Iterator;

/**
 *
 * @author User
 */
public class ImmutableIteratorView<T> implements Iterator<T> {

    Iterator<T> wrapee;

    public ImmutableIteratorView(Iterator<T> wrapee) {
        this.wrapee = wrapee;
    }

    @Override
    public boolean hasNext() {
        return wrapee.hasNext();
    }

    @Override
    public T next() {
        return wrapee.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not Supported.");
    }
}
