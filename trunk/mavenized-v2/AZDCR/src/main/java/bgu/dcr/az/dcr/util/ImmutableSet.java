package bgu.dcr.az.dcr.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * extends hashset - all the mutational operations are throwing unsupported operation exception
 * @author bennyl
 * @param <T> 
 */
public class ImmutableSet<T> extends HashSet<T> {

    public ImmutableSet(Collection<T> data) {
        for (T o : data) super.add(o);
    }

    public boolean add(T arg0) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    public boolean addAll(java.util.Collection<? extends T> c) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    ;
	
	@Override
    public Iterator<T> iterator() {
        final Iterator<T> i = super.iterator();
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public T next() {
                return i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("cannot modify to imuuteable set");
            }
        };
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }
    
}
