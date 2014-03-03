/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.pivot.model;

import bgu.dcr.az.orm.api.Data;

/**
 *
 * @author User
 * @param <T>
 */
public interface TableData extends Data {

    Headers getColumnHeaders();

    Headers getRowHeaders();

    public static interface Headers extends Iterable<Object[]> {
        Object[] getHeader(int i);
        
        int numberOfHeaders();
    }
    
    public static interface HeaderExtractor {
        String extract(Object[] o);
    }
}
