/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.utils;

import bgu.dcr.az.execs.AbstractProc;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author bennyl
 */
public class CreazyWakingProc extends AbstractProc {

    int steps = 0;
    int maxSteps;
    int numProcs;

    public CreazyWakingProc(int pid, int maxSteps, int numProcs) {
        super(pid);
        this.maxSteps = maxSteps;
        this.numProcs = numProcs;
    }

    @Override
    protected void start() {
        System.out.println("Process: " + pid() + " Started!");
    }

    @Override
    protected void quota() {
//        System.out.println("Process: " + pid() + " Quota!");

        int[] array = new int[maxSteps];
        for (int i = 0; i < maxSteps; i++) {
            array[i]++;
        }

        if (array.length > 0 && array[0] == 5) {
            System.out.println("This can never happened!");
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
//            System.out.println("Bye: " + pid());
            terminate();
        } else {
            sleep();
        }

        boolean atleastOne = false;
        for (int i = 0; i < numProcs; i++) {
            if (!atleastOne) {
                atleastOne = wakeup(i);
            } else {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    wakeup(i);
                }
            }
        }

    }

    @Override
    protected boolean wakeup(int pid) {
        if (super.wakeup(pid)) {
//            System.out.println("" + pid() + " waking: " + pid);
            return true;
        }

        return false;
    }

}
