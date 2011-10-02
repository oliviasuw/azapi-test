/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ds;

import java.util.Date;

/**
 *
 * @author bennyl
 */
public class TimeDelta {
    long startMilis = -1;
    long endMilis = -1;
    
    public long getDeltaMilis(){
        if (startMilis == -1){
            return 0;
        }
        
        if (endMilis == -1){
            return System.currentTimeMillis() - startMilis;
        }
        
        return endMilis - startMilis;
    }
    
    public void setStart(Date d){
        startMilis = d.getTime();
    }
    
    public void setStart(){
        startMilis = System.currentTimeMillis();
    }
    
    public void setEnd(){
        endMilis = System.currentTimeMillis();
    }
    
    public void setEnd(Date d){
        endMilis = d.getTime();
    }

    @Override
    public String toString() {
        return "" + ((double)getDeltaMilis()/1000.0) + "sec";
    }
    
}
