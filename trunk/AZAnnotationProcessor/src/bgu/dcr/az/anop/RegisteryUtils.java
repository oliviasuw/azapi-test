/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop;

/**
 *
 * @author User
 */
public class RegisteryUtils {

    private static Registery defaultRegistery;

    public static Registery getDefaultRegistery() {
        if (defaultRegistery == null) {
            try {
                defaultRegistery = (Registery) Class.forName("bgu.dcr.autogen.CompiledRegistery").newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        return defaultRegistery;
    }

}
