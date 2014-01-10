/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.texen;

import java.util.Random;

/**
 *
 * @author User
 */
public class CrushingTickable extends AbstractTestingTickable{

    Random r = new Random(1234);
    int numberOfTicksToCrushAt = 1000;
    
    public CrushingTickable(int id, TickablesExecutorService exec, int numTickables, TestResult result) {
        super(id, exec, numTickables, result);
    }

    @Override
    public void _tick() {
        if (result.numberOfTicks.get() == numberOfTicksToCrushAt){
            throw new RuntimeException("Crushing!!!");
        }else {
            requestTick(r.nextInt(numTickables));
        }
    }

}
