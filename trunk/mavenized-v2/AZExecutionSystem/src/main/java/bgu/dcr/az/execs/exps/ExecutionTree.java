/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.modules.info.InfoStream;

/**
 * represents a sub tree of executions, the children in this sub tree are the
 * executions that are needed to be ended before this execution can itself end
 * (or execute itself if it make sense)
 *
 * @author bennyl
 */
public abstract class ExecutionTree extends ModuleContainer implements Iterable<ExecutionTree> {

    private InfoStream istream;

    @Override
    public void install(Class<? extends Module> moduleKey, Module module) {
        if (moduleKey == ExperimentProgressInspector.class && parent() != null) { //experiment progress enhancers should allways reside under the modular experiment itself.
            parent().install(moduleKey, module);
        } else {
            super.install(moduleKey, module); 
        }
    }

    public InfoStream infoStream() {
        return istream == null ? istream = require(InfoStream.class) : istream;
    }

    /**
     * @return the name of this execution sub tree. (just a string
     * identification to allow ui clients to navigate this element)
     */
    public abstract String getName();

    /**
     * @return the number of child executions
     */
    public abstract int numChildren();

    /**
     * return the i'th child execution
     *
     * @param index
     * @return
     */
    public abstract ExecutionTree child(int index);

    /**
     * execute all the children on this execution tree and than perform the
     * execution that is needed to this node
     */
    public abstract void execute() throws ExperimentFailedException;

    /**
     * @return the amount of executions that are needed to run, note that this
     * is not have to be equal to the number of nodes in the tree as there can
     * be nodes that doesnt have to be executed and are only there to aggregate
     * other executions and common modules - these should not get count.
     */
    public abstract int countExecutions();
}
