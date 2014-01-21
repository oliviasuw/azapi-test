/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.utils;

import bgu.dcr.az.execs.AbstractProc;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author bennyl
 */
public class ForkingProcess extends AbstractProc {

    private static final AtomicInteger numberOfForks = new AtomicInteger(100000);

    public ForkingProcess(int pid) {
        super(pid);
    }

    @Override
    protected void start() {
        System.out.println("Process: " + pid() + " Started!");
    }

    @Override
    protected void quota() {
        
        int[] array = new int[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] ++;
        }
        
        if (array[0] == 2){
            System.out.println("");
        }
        
        if (ThreadLocalRandom.current().nextDouble() > 0.3 && numberOfForks.getAndDecrement() > 0) {
            exec(new ForkingProcess(nextProcessId()));
        } else {
            terminate();
        }
    }

}
