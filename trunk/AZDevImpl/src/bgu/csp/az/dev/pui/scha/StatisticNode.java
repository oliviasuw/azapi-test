/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui.scha;

import bc.dsl.JavaDSL.Fn;
import bc.dsl.JavaDSL.Fn1;
import bc.swing.models.GenericTreeModel.Node;
import bgu.csp.az.api.Statistic;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 *
 * @author bennyl
 */
public class StatisticNode extends Node {

    List<Statistic> roots = new LinkedList<Statistic>();
    List<StatisticNode> children = new LinkedList<StatisticNode>();
    LinkedList<Listener> listeners = new LinkedList<Listener>();

    public List<Statistic> getRoots() {
        return roots;
    }

    public StatisticNode(String name, Node parent) {
        super(name, parent);
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void clearListeners() {
        listeners = new LinkedList<Listener>();
    }

    public void fireRootAdded(Statistic root) {
        for (Listener l : listeners) {
            l.onRootAdded(root);
        }
    }

    public synchronized void addStatisticRoot(Statistic root) {
        if (roots.isEmpty()) {
            createStracture(root);
        }

        roots.add(root);

        Iterator<StatisticNode> myChildI = children.iterator();
        for (Statistic child : root.getChildren().values()) {
            try {
                myChildI.next().addStatisticRoot(child);
            } catch (NoSuchElementException exe) {
                exe.printStackTrace();
                //TODO WHY????
            }
        }

        fireRootAdded(root);
    }

    public synchronized void safeStatisticsIteration(Fn1<Void, Statistic> fn) {
        for (Statistic r : roots) {
            fn.invoke(r);
        }
    }

    @Override
    public List<StatisticNode> getChildren() {
        return children;
    }

    private void createStracture(Statistic root) {
        for (Entry<String, Statistic> child : root.getChildren().entrySet()) {
            final StatisticNode statisticNode = new StatisticNode(child.getKey(), this);
            this.children.add(statisticNode);
        }
    }

    public static interface Listener {

        void onRootAdded(Statistic root);
    }
}
