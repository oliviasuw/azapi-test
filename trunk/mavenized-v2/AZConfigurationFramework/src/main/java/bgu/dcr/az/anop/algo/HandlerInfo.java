/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.algo;

import bgu.dcr.az.anop.conf.JavaDocInfo;
import java.util.Collection;

/**
 * information of message handler - mainly for ui and debugging porpuses
 *
 * @author Benny Lutati
 */
public interface HandlerInfo {

    JavaDocInfo doc();

    String hanlderOf();

    ParameterInfo[] parameters();
}
