/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.player.impl.actions;

import bgu.dcr.az.vis.player.api.Action;
import bgu.dcr.az.vis.player.impl.Location;
import bgu.dcr.az.vis.tools.easing.DoubleEasingVariable;
import bgu.dcr.az.vis.tools.easing.EasingVariableDoubleBased;
import bgu.dcr.az.vis.tools.easing.LinearDouble;

/**
 *
 * @author Zovadi
 */
public class MoveAction extends SingleEntityAction {

    private final DoubleEasingVariable xEasingVar;
    private final DoubleEasingVariable yEasingVar;
    protected final Location from;
    protected final Location to;

    public MoveAction(long entityId, Location from, Location to) {
        super(entityId);
        this.from = from;
        this.to = to;
        xEasingVar = new DoubleEasingVariable(new LinearDouble(), EasingVariableDoubleBased.EasingFunctinTypeDouble.EASE_IN, from.getX(), to.getX());
        yEasingVar = new DoubleEasingVariable(new LinearDouble(), EasingVariableDoubleBased.EasingFunctinTypeDouble.EASE_IN, from.getY(), to.getY());
    }

    @Override
    public void _initialize(long transitionMillis) {
        xEasingVar.change(to.getX(), transitionMillis);
        yEasingVar.change(to.getY(), transitionMillis);
    }
    
    @Override
    protected void _update(double percentage) {
        xEasingVar.update(percentage);
        yEasingVar.update(percentage);
        
        getEntity().locationProperty().get().xProperty().set(xEasingVar.getCurrentValue());
        getEntity().locationProperty().get().yProperty().set(yEasingVar.getCurrentValue());
    }

    @Override
    public Action subAction(double percentageFrom, double percentageTo) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();

        return new MoveAction(getEntityId(),
                new Location(from.getX() + dx * percentageFrom, from.getY() + dy * percentageFrom),
                new Location(from.getX() + dx * percentageTo, from.getY() + dy * percentageTo));
    }

}
