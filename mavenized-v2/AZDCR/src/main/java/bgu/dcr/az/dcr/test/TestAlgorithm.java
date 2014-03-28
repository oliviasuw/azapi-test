/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.dcr.test;

import bgu.dcr.az.dcr.api.Agent;
import bgu.dcr.az.dcr.api.annotations.Algorithm;
import bgu.dcr.az.dcr.api.annotations.WhenReceived;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
@Algorithm("TESTING")
public class TestAlgorithm extends Agent{

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @WhenReceived("A")
    public void handleA(int a, Integer b){
        
    }
    @WhenReceived("B")
    public void handleB(int[] a, Integer[] b){
        
    }
    
    @WhenReceived("C")
    public void handleC(List<Integer> a){
        
    }
    
    @WhenReceived("C_2")
    public void handleC(int a){
        
    }
    
    public static void main(String[] args) {
        try {
            Class.forName("bgu.dcr.az.dcr.autogen.ext_sim_agents_dcop_SBBAgent");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TestAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
