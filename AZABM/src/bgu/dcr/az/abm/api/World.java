/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.api;

import bgu.dcr.az.mas.impl.HasSolution;
import bgu.dcr.az.mas.stat.InfoStream;
import java.util.Collection;

/**
 *
 * @author Eran
 */
public interface World extends HasSolution {

    InfoStream infoStream();

    Collection<Class<? extends Behavior>> behaviors();

    Collection<Service> services();

    Collection<ABMAgent> agents();
}
