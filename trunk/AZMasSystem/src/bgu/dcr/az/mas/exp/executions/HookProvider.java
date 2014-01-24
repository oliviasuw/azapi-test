/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp.executions;

/**
 *
 * @author User
 */
public interface HookProvider<T> {

    void register(T hook);
}
