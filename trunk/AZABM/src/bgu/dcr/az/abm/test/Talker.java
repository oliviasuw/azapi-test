/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.test;

import bgu.dcr.az.abm.api.AgentData;
import bgu.dcr.az.abm.impl.AbstractAgentData;

/**
 *
 * @author bennyl
 */
public class Talker extends AbstractAgentData {

    private boolean shouldTalk = false;

    public void setShouldTalk(boolean shouldTalk) {
        this.shouldTalk = shouldTalk;
    }

    public boolean isShouldTalk() {
        return shouldTalk;
    }

}
