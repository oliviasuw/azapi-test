/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.srccon.util;

import java.io.File;

/**
 *
 * @author Administrator
 */
public class Systems {

    public static File executionPath() {
        return new File(Systems.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
    
    public static File pwd(){
        return new File(System.getProperty("user.dir"));
    }
    
    
    public static void main(String[] args){
//        System.out.println(Systems.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println(System.getProperty("java.class.path"));
        
    }
}
