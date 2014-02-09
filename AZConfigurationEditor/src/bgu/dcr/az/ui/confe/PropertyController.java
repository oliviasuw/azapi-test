/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;

/**
 *
 * @author Shl
 */
public interface PropertyController {

    public void setModel(Configuration conf, boolean readOnly);
    
    public void setModel(Property property, boolean readOnly);

}
