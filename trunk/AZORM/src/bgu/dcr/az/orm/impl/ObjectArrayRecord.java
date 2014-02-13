/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author User
 */
public class ObjectArrayRecord implements Record {

    private Object[] array;

    public ObjectArrayRecord(Object... array) {
        this.array = array;
    }

    @Override
    public Object get(int index) {
        return array[index];
    }

    @Override
    public int length() {
        return array.length;
    }

    @Override
    public Iterator<Object> iterator() {
        return Arrays.asList(array).iterator();
    }

}
