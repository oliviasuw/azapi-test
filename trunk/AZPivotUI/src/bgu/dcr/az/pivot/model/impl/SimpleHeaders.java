/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model.impl;

import bgu.dcr.az.pivot.model.TableData.Headers;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author bennyl
 */
public class SimpleHeaders implements Headers {

    private final Object[][] headers;

    public SimpleHeaders(Object[][] headers) {
        this.headers = headers;

        Arrays.sort(this.headers, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return Arrays.toString(o1).compareTo(Arrays.toString(o2));
            }
        });

    }

    @Override
    public Object[] getHeader(int i) {
        return headers[i];
    }

    @Override
    public int numberOfHeaders() {
        return headers.length;
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < headers.length;
            }

            @Override
            public Object[] next() {
                return headers[i++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

}
