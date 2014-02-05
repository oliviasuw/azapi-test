/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author User
 */
public class ImmutableCollectionView<T> implements Collection<T> {

    Collection<T> wrapee;

    public ImmutableCollectionView(Collection<T> wrapee) {
        this.wrapee = wrapee;
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
        return new ImmutableIteratorView<>(wrapee.iterator());
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
        throw new UnsupportedOperationException("Not Supported.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not Supported.");

    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapee.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not Supported.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not Supported.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not Supported.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not Supported.");
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
