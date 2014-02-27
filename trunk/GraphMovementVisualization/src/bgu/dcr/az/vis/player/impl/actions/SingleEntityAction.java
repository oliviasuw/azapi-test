/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.actions;

import bgu.dcr.az.vis.player.api.Action;
import bgu.dcr.az.vis.player.api.Entity;
import bgu.dcr.az.vis.player.api.Player;

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
        _initialize();
    }

    protected abstract void _initialize();
    
    @Override
    public final void update(double percentage) {
        _update(percentage);
        entity.draw(player.getScene());
    }

    protected abstract void _update(double percentage);
}
