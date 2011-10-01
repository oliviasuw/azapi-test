/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bam.utils.evt.EventManager;
import bgu.csp.az.api.infra.EventPipe;
import com.google.gson.JsonElement;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class EventDistributer extends Thread{
    EventPipe pipe;
    volatile boolean close;

    public EventDistributer(EventPipe pipe) {
        this.close = false;
        this.pipe = pipe;
        setDaemon(true);
    }

    @Override
    public void run() {
        List<JsonElement> all;
        while (!isInterrupted()){
            try {
                if (! close){
                    pipe.waitForEvents();
                }else {
                    sleep(2000); //wait for all the events to come
                }
                all = pipe.takeAll();
                
                for (JsonElement e : all){
                    EventManager.INSTANCE.fire(e);
                }
                
                if (close) break;
            } catch (InterruptedException ex) {
                interrupt();
            }
        }
    }
    
    public void close(){
        close = true;
    }

    
}
