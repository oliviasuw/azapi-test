/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

/**
 *
 * @author User
 */
public interface Record extends Iterable<Object> {

    Object get(int index);

    int length();
}
