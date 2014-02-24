/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.api;

import javafx.concurrent.Service;

/**
 *
 * @author Zovadi
 */
public interface Player {

    VisualScene getScene();
    
    Service play(Frame frame);
}
