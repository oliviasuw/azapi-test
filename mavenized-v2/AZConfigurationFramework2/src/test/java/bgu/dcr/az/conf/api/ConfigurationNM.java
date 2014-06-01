/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.api;

import bgu.dcr.az.conf.registery.Register;

/**
 *
 * @author bennyl
 */
@Register("test-conf-nm")
public class ConfigurationNM extends ConfigurationNMParent {

    String hello;

    /**
     * @propertyName nmHello
     * @return 
     */
    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

}
