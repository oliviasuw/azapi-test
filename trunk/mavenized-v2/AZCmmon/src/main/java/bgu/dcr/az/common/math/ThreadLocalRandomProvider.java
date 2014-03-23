/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.math;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bennyl
 */
public class ThreadLocalRandomProvider {

    /**
     * The actual ThreadLocal
     */
    private final ThreadLocal<LocalRandom> localRandom;

    public ThreadLocalRandomProvider(final long seed) {

        localRandom
                = new ThreadLocal<LocalRandom>() {
                    protected LocalRandom initialValue() {
                        return new LocalRandom(seed + Thread.currentThread().getId());
                    }
                };
    }

    public LocalRandom get() {
        return localRandom.get();
    }

}
