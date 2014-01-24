/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.conf;

/**
 * this class represents a property value configuration it has the ability to
 * generate the "real" value on request
 *
 * @author User
 */
public interface PropertyValue {

    <T> T create(TypeInfo type) throws ConfigurationException;
}
