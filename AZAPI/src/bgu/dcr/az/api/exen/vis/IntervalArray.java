/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class IntervalArray<T> {

    ArrayList<Interval<T>> array = new ArrayList<Interval<T>>();

    public IntervalArray() {
    }

    public void add(T data, long start, long end) {
        array.add(new Interval<T>(start, end, data));
    }

    public T get(long pos) {
        int a = 0;
        int b = array.size();

        while (true) {
            int mid = (a + b) / 2;
            final Interval<T> interval = array.get(mid);
            if (interval.isIn(pos)) {
                return array.get(mid).data;
            }

            if (interval.graterThen(pos)) {
                a = mid + 1;
            } else {
                b = mid - 1;
            }
            
            if (b < a) return null;
        }
    }

    private static class Interval<T> {

        public long start;
        public long end;
        public T data;

        public Interval(long start, long end, T data) {
            this.start = start;
            this.end = end;
            this.data = data;
        }

        public boolean isIn(long what) {
            return what >= start && what <= end;
        }

        public boolean graterThen(long what) {
            return what > end;
        }
    }
}
