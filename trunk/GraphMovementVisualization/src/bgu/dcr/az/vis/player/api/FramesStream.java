/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.api;

/**
 *
 * @author Zovadi
 */
public interface FramesStream {

    void writeFrame(Frame frame);

    Frame readFrame();
    
//    Frame getFrame(long index);
//    
//    long numberOfFrames();
}
