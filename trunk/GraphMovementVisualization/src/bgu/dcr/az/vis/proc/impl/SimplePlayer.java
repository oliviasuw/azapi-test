/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.proc.impl;

import bgu.dcr.az.vis.proc.api.Frame;
import bgu.dcr.az.vis.proc.api.Player;
import bgu.dcr.az.vis.proc.api.VisualScene;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Shl
 */
public class SimplePlayer implements Player {

    @Override
    public VisualScene getScene() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Service play(Frame frame) {
        return new Service() {
            @Override
            protected Task createTask() {
                return frameToTask(frame);
            }
        };
    }
    
    private Task frameToTask(Frame frame) {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                frame.forEach(a -> a.execute(getScene()));
                return null;
            }
        };
    }
    
}
