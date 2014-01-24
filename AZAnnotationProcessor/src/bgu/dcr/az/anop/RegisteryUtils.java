/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop;

import bgu.dcr.az.anop.base.RegisteryImpl;

/**
 *
 * @author User
 */
public class RegisteryUtils {

    private static Registery defaultRegistery;

    public static Registery getDefaultRegistery() {
        if (defaultRegistery == null) {
            defaultRegistery = new RegisteryImpl();
        }

        return defaultRegistery;
    }

}
