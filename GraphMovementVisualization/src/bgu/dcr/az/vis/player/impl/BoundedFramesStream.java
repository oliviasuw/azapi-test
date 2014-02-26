/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.api.Frame;
import bgu.dcr.az.vis.player.api.FramesStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zovadi
 */
public class BoundedFramesStream implements FramesStream {

    private final int queueMaxSize;
    private final ConcurrentLinkedQueue<Frame> framesQueue;
    private final Semaphore queueSemaphore;

    public BoundedFramesStream(int queueMaxSize) {
        this.queueMaxSize = queueMaxSize;
        this.framesQueue = new ConcurrentLinkedQueue<>();
        this.queueSemaphore = new Semaphore(queueMaxSize - 1);
    }

    public int getQueueMaxSize() {
        return queueMaxSize;
    }

    public int getNumberOfPendingFrames() {
        return framesQueue.size();
    }

    @Override
    public void writeFrame(Frame frame) {
        try {
            queueSemaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(BoundedFramesStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        framesQueue.add(frame);
    }

    @Override
    public Frame readFrame() {
        final Frame frame = framesQueue.poll();
        queueSemaphore.release();
        return frame;
    }
}
