/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.modules;

/**
 *
 * @author bennyl
 * @param <T> the type of the module container (for ease of implementation)
 */
public interface Module<T extends ModuleContainer> {

    default void installInto(T mc) {

    }
}
