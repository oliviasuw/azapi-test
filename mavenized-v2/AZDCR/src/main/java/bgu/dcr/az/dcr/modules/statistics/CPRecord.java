/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.orm.api.DBRecord;

/**
 *
 * @author User
 */
public class CPRecord implements DBRecord{
    public String algorithm_instance;
    public double rvar;
    public String test;
}
