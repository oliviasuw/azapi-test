/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.utils.ConfigurationUtils;
import nu.xom.Builder;
import nu.xom.Document;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class DCRExperimentTest {

    public DCRExperimentTest() {
    }

    @Test
    public void testBasicConfiguration() throws Exception {
        Builder b = new Builder();
        Document d = b.build(DCRExperimentTest.class.getResourceAsStream("test.xml"));
        Configuration conf = ConfigurationUtils.fromXML(d.getRootElement());
        
        final DCRExperimentDef exp = conf.create();
        System.out.println("Got: " + exp);
        exp.execute();
    }

}
