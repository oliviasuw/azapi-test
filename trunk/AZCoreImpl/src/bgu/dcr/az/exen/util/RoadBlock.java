/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.util;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * synchronization tool - every thread can try to pass the block but the one that call remove will free all of
 * them + no more threads will get blocked
 * 
 * @author Administrator
 */
public class RoadBlock {
   volatile boolean free = false;
   Semaphore block = new Semaphore(0);
   AtomicInteger waiting = new AtomicInteger(0);
   
   public void pass() throws InterruptedException{
       waiting.incrementAndGet();
       if (!free){    
           block.acquire();
       }
       
       waiting.decrementAndGet();
   }
   
   public void remove(){
       free = true;
       block.release(waiting.get());
   }
}
