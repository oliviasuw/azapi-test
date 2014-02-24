/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.actions;

/**
 *
 * @author Shl
 */
public abstract class SingleEntityAction extends SimpleAction {

    private final long entityId;

    public SingleEntityAction(long entityId, double duration) {
        super(duration);
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }
    
}
