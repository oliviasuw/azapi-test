/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Eran
 */
public class State {

    private String name;
    private LinkedList<Behavior> behaviors;

    /**
     * create a new state with the given name.
     *
     * @param name
     */
    public State(String name) {
        this.name = name;
        this.behaviors = new LinkedList<>();
    }

    /**
     * return state's name
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * add behaviors into the current state (the number of given behaviors is
     * not limited)
     *
     * @param behaviors
     */
    public void add(Behavior... behaviors) {
        this.behaviors.addAll(Arrays.asList(behaviors));
    }

    /**
     * return a collection containing the types of behaviors in the given state.
     *
     * @return
     */
    public Collection<Class<? extends Behavior>> getBehaviors() {
        LinkedList<Class<? extends Behavior>> dcopy = new LinkedList<>();
        this.behaviors.forEach((b) -> dcopy.add(b.getClass()));
        return dcopy;
    }

    /**
     * execute the behaviors, one by one. stops whether all behaviors have been
     * executed, or the current state has been changed.
     *
     * @return
     */
    public void applyBehaviors() {
        for (Behavior b : this.behaviors)
            b.behave(getName());
    }
}
