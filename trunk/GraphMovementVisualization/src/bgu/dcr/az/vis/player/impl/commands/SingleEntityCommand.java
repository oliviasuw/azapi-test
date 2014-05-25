/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.commands;

import bgu.dcr.az.vis.player.api.Command;
import bgu.dcr.az.vis.player.api.Entity;
import bgu.dcr.az.vis.player.api.Player;

/**
 *
 * @author Shl
 */
public abstract class SingleEntityCommand implements Command {

    private final long entityId;
    private Player player;
    private Entity entity;

    public SingleEntityCommand(long entityId) {
        this.entityId = entityId;

    }

    public long getEntityId() {
        return entityId;
    }

    public Entity getEntity() {
        return entity;
    }

    public final void initialize(Player player) {
        this.player = player;
        entity = (Entity) player.getQuery().getById(String.valueOf(entityId));

        //we assume that something that can be moved is an entity?
        _initialize();
    }

    protected abstract void _initialize();

    @Override
    public final void update(double percentage) {
        _update(percentage);
    }

    protected abstract void _update(double percentage);
}
