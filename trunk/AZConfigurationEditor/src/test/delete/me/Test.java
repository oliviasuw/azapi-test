/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.delete.me;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.anop.reg.RegisteryUtils;

/**
 *
 * @author Shl
 */
public class Test {

    public static void main(String[] args) throws Exception {
        SomeClass c = new SomeClass();
        c.setJ("bla");
        c.setI(5);
        Configuration conf = ConfigurationUtils.load(c);
        
        for (Property p : conf){
            System.out.println("Have property: " + p);
            System.out.println("The property javadoc: " + p.doc());
        }
        
        
//        Configuration conf = RegisteryUtils.getDefaultRegistery().getConfiguration(SomeClass.class);
//        conf.get("the-i").set(new FromStringPropertyValue("7"));
        System.out.println("XML:\n" + ConfigurationUtils.toXML(conf).toXML());

    }

}
