/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.collections;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
            Iterator<T> ib = b.iterator();

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

    public static <T> List<T> toList(Iterable<T> iall) {
        return toList(iall, new LinkedList());
    }

    public static <T> List<T> toList(Iterable<T> iall, List ll) {
        iall.forEach(ll::add);
        return ll;
    }

    public static <T> Set<T> toSet(Iterable<T> iall) {
        HashSet ll = new HashSet();
        iall.forEach(ll::add);
        return ll;
    }

    public static <T> Stream<T> stream(Iterable<T> i) {
        return StreamSupport.stream(i.spliterator(), false);
    }

    public static <T> Iterable<T> sorted(Iterable<T> i) {
        return () -> stream(i).sorted().iterator();
    }
    
    public static <T> Iterable<T> sorted(Iterable<T> i, Comparator<T> c) {
        return () -> stream(i).sorted(c).iterator();
    }
}
