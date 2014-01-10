/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.texen;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author User
 */
public class TestResult {

    public AtomicInteger numberOfTicks = new AtomicInteger(0);
    public TickablesExecutorService.TerminationStatus executionResult;
    public volatile int[] numberOfTicksPerTickable;
    public int numberOfIdleDetections;

    public TestResult(int numberOfTickables) {
        numberOfTicksPerTickable = new int[numberOfTickables];
    }
    
    
    
}
