/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.util;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collection;

/**
 *
 * @author User
 */
public class ImmutableIntSetView implements IntSet {

    IntSet delegate;

    public ImmutableIntSetView(IntSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public IntIterator iterator() {
        final IntIterator i = delegate.iterator();

        return new IntIterator() {

            @Override
            public int nextInt() {
                return i.nextInt();
            }

            @Override
            public int skip(int n) {
                return i.skip(n);
            }

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public Integer next() {
                return i.next();
            }
        };
    }

    @Override
    public boolean remove(int k) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public IntIterator intIterator() {
        return iterator();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return delegate.toArray(arg0);
    }

    @Override
    public boolean contains(int key) {
        return delegate.contains(key);
    }

    @Override
    public int[] toIntArray() {
        return delegate.toIntArray();
    }

    @Override
    public int[] toIntArray(int[] arg0) {
        return delegate.toIntArray(arg0);
    }

    @Override
    public int[] toArray(int[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(int key) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public boolean rem(int key) {
        throw new UnsupportedOperationException("rem");
    }

    @Override
    public boolean addAll(IntCollection c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean containsAll(IntCollection c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean removeAll(IntCollection c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    public boolean retainAll(IntCollection c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public boolean add(Integer e) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

}
