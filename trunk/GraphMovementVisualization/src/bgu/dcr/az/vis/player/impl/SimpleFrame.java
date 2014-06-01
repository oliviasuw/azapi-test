/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl;

import bgu.dcr.az.vis.player.api.Command;
import bgu.dcr.az.vis.player.api.Frame;
import bgu.dcr.az.vis.player.api.Player;
import bgu.dcr.az.vis.player.impl.commands.MoveCommand;
import bgu.dcr.az.vis.player.impl.entities.DefinedSizeSpriteBasedEntity;
import bgu.dcr.az.vis.player.impl.entities.SimpleEntity;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import data.map.impl.wersdfawer.groupbounding.HasId;
import java.util.ArrayList;

/**
 *
 * @author Zovadi
 */
public class SimpleFrame implements Frame {

    private final ArrayList<Command> actions;

    public SimpleFrame() {
        this.actions = new ArrayList<>();
    }

    @Override
    public SimpleFrame addCommand(Command action) {
        actions.add(action);
        return this;
    }
    
    @Override
    public void initialize(Player player) {
        actions.forEach(a -> a.initialize(player));
    }
    
    @Override
    public void update(double percentage, GroupBoundingQuery query) {
        for (Command a : actions) {
            a.update(percentage);
            if (a instanceof MoveCommand) {
                MoveCommand moveAction = (MoveCommand) a;
                DefinedSizeSpriteBasedEntity entity = (DefinedSizeSpriteBasedEntity) query.getById(String.valueOf(moveAction.getEntityId()));
                String[] groupDetails = query.getGroupDetails(String.valueOf(moveAction.getEntityId()));
                query.remove(entity, moveAction.getFrom().getX(), moveAction.getFrom().getY());
                query.addToGroup(groupDetails[0], groupDetails[1], moveAction.getTo().getX(), moveAction.getTo().getY(), entity.getRealWidth(), entity.getRealHeight(), entity);
            }
        }
    }

}
