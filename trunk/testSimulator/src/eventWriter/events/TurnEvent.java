/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eventWriter.events;

import eventWriter.SimulatorEvent;

/**
 *
 * @author Eran
 */
public class TurnEvent implements SimulatorEvent {
    private int id;
    private String sourceFromNode;
    private String sourceToNode;
    private String targerFromNode;
    private String targetToNode;

    public TurnEvent() {
    }
    
    public TurnEvent(int id, String sourceFromNode, String sourceToNode, String targerFromNode, String targetToNode) {
        this.id = id;
        this.sourceFromNode = sourceFromNode;
        this.sourceToNode = sourceToNode;
        this.targerFromNode = targerFromNode;
        this.targetToNode = targetToNode;
    }

    public int getId() {
        return id;
    }

    public String getSourceFromNode() {
        return sourceFromNode;
    }

    public String getSourceToNode() {
        return sourceToNode;
    }

    public String getTargetFromNode() {
        return targerFromNode;
    }

    public String getTargetToNode() {
        return targetToNode;
    }
}
