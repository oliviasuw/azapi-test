/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.utils;

import bgu.dcr.az.execs.AbstractProc;
import java.util.LinkedList;

/**
 *
 * @author bennyl
 */
public class SimpleSteppingProc extends AbstractProc {

    int steps = 0;
    int maxSteps;

    public SimpleSteppingProc(int pid, int maxSteps) {
        super(pid);
        this.maxSteps = maxSteps;
    }

    @Override
    protected void start() {
        System.out.println("Process: " + pid() + " Started!");
    }

    @Override
    protected void quota() {
//        System.out.println("Process: " + pid() + " Quota: " + steps + "!");

        int[] array = new int[maxSteps];
        for (int i = 0; i < maxSteps; i++) {
            array[i]++;
        }

        if (array.length > 0 && array[0] == 5) {
            System.out.println("This can never happened!");
        }

        if (steps++ == maxSteps) {
            terminate();
        } else {
            sleep();
        }
    }

    @Override
    protected void onIdleDetected() {
//        System.out.println("Process: " + pid() + " Resolving idle");
        wakeup(pid());
    }

}
