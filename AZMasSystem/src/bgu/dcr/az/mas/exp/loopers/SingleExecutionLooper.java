/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp.loopers;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.mas.exp.Looper;
import java.util.Collection;

/**
 *
 * @author User
 */
public class SingleExecutionLooper implements Looper {

    @Override
    public int count() {
        return 1;
    }

    @Override
    public void configure(int i, Configuration[] configurations) {
    }

}
