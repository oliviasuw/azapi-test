/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.collections.immut;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class ImmutableSetView<T> implements Set<T> {

    private Set<T> wrapee;

    public ImmutableSetView(Set<T> wrapee) {
        this.wrapee = wrapee;
        if (wrapee == null) {
            wrapee = Collections.EMPTY_SET;
        }
    }

    @Override
    public int size() {
        return wrapee.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapee.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapee.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Iterator<T> it = wrapee.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("immutable set.");
            }
        };
    }

    @Override
    public Object[] toArray() {
        return wrapee.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrapee.toArray(a);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("immutable set.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("immutable set.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapee.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("immutable set.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("immutable set.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("immutable set.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("immutable set.");
    }

    @Override
    public boolean equals(Object o) {
        return wrapee.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapee.hashCode();
    }
}
