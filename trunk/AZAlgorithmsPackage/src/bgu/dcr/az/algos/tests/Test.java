/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.algos.tests;

import bgu.dcr.az.mas.exp.ExperimentUtils;

/**
 *
 * @author User
 */
public class Test {

    public static void main(String[] args) throws Exception {
        ExperimentUtils.executeExperiment(Test.class.getResourceAsStream("test.xml"));
    }
}
