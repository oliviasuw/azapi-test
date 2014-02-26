/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.actions;

import bgu.dcr.az.vis.proc.api.Action;
import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.Player;

/**
 *
 * @author Shl
 */
public abstract class SingleEntityAction implements Action {

    private final long entityId;
    private Player player;
    private Entity entity;

    public SingleEntityAction(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public Entity getEntity() {
        return entity;
    }
    
    @Override
    public final void initialize(Player player) {
        this.player = player;
        entity = player.getScene().getEntity(entityId);
        _initialize(player.millisPerFrameProperty().get());
    }

    protected abstract void _initialize(long transitionMillis);
    
    @Override
    public final void update() {
        _update();
        entity.draw(player.getScene());
    }

    protected abstract void _update();
}
