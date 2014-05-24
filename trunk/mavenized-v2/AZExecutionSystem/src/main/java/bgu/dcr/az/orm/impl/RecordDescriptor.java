/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.orm.impl;

/**
 *
 * @author user
 */
public interface RecordDescriptor {

    String[] fields();

    Object get(int idx, Object from);

    Class type(int idx);

    Object identifier();

    default String field(int i) {
        return fields()[i];
    }
    
}
