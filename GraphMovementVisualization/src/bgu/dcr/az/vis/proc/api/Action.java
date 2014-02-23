/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.api;

import java.util.Collection;
import javafx.animation.KeyValue;

/**
 *
 * @author Zovadi
 */
public interface Action {

    double getDurationFraction();

    Collection<KeyValue> apply(Entity entity);

    ActionSequence split(double duration);
}
