/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.api;

import java.util.List;

/**
 *
 * @author User
 */
public interface TypeInfo {

    List<TypeInfo> getGenericParameters();

    Class getType();

}
