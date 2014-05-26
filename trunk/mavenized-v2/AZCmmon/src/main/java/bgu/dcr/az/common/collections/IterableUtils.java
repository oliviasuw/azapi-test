/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.collections;

import java.util.Iterator;

/**
 *
 * @author bennyl
 */
public class IterableUtils {

    public static <T> Iterable<T> unmodifiableIterable(Iterable<T> i) {
        return () -> {
            Iterator<T> iter = i.iterator();
            return new Iterator<T>() {

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public T next() {
                    return iter.next();
                }
            };
        };
    }

    public static <T> Iterable<T> combine(Iterable<T> a, Iterable<T> b) {
        return () -> {
            Iterator<T> ia = a.iterator();
            Iterator<T> ib = a.iterator();

            return new Iterator<T>() {

                @Override
                public boolean hasNext() {
                    return ia.hasNext() || ib.hasNext();
                }

                @Override
                public T next() {
                    return ia.hasNext() ? ia.next() : ib.next();
                }
            };

        };
    }
}
