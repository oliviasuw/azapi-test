/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.mdelay;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.infra.Execution;

/**
 *
 * @author bennyl
 */
public interface MessageDelayer {
    void initialize(Execution ex);
    int getInitialTime();
    int extractTime(Message m);
    void addDelay(Message m, int from, int to);
}
