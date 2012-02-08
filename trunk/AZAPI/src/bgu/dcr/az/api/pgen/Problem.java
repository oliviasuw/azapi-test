package bgu.dcr.az.api.pgen;

import bgu.dcr.az.api.ImmutableProblem;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.Assignment;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract class for problems that should let you build any type of problem 
 * @author guyafe, edited by bennyl
 */
public abstract class Problem implements Serializable, ImmutableProblem {

    private HashMap<String, Object> metadata = new HashMap<String, Object>();
    protected int numvars;
    protected ImmutableSet<Integer> domain;
    protected HashMap<Integer, Set<Integer>> neighbores = new HashMap<Integer, Set<Integer>>();
    protected HashMap<Integer, Set<Integer>> immutableNeighbores = new HashMap<Integer, Set<Integer>>();
    //private Semaphore immutableNeighborsWriteLock = new Semaphore(1);
    private ReentrantReadWriteLock immutableNeighborsLock = new ReentrantReadWriteLock();
    //    protected HashMap<Integer, Boolean> constraints = new HashMap<Integer, Boolean>();
    protected ProblemType type;
    protected int maxCost = 0;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        constraintsToString(sb);
        problemToString(sb);
        return sb.toString();
    }

    private void problemToString(StringBuilder sb) {

        int maxCostSize = 0;
        int domainSize = 0;
        if (maxCost == 1) {
            maxCostSize = 1;
        } else {
            maxCostSize = (int) Math.log10(maxCost);
        }
        if (getDomainSize(0) == 1) {
            domainSize = 1;
        } else {
            domainSize = (int) Math.log10(getDomainSize(0) - 1);
        }
        String tmpForMaxCost[] = new String[maxCostSize + 2];
        String tmpForDomainSize[] = new String[domainSize + 2];
        String tmpLineForMaxCost = "";
        String tmpLineForDomainSize = "";
        String line = "";
        for (int i = 0; i < tmpForMaxCost.length; i++) {
            tmpForMaxCost[i] = "";
        }
        for (int i = 0; i < tmpForDomainSize.length; i++) {
            tmpForDomainSize[i] = "";
        }
        for (int i = 0; i < tmpForMaxCost.length; i++) {
            for (int j = 0; j < i; j++) {
                tmpForMaxCost[i] += " ";
            }
            tmpLineForMaxCost += "-";
        }
        for (int i = 0; i < tmpForDomainSize.length; i++) {
            for (int j = 0; j < i; j++) {
                tmpForDomainSize[i] += " ";
            }
            tmpLineForDomainSize += "-";
        }
        sb.append("The Problem:\n");
        for (int i = 0; i < getNumberOfVariables(); i++) {
            for (int j = 0; j < getNumberOfVariables(); j++) {
                if (!isConstrained(i, j)) {
                    continue;
                }
                if (i < j && type() != ProblemType.ADCOP) {
                    continue;
                }
                sb.append("\n").append("Agent ").append(i).append(" --> Agent ").append(j).append("\n").append("\n");
                sb.append(tmpForDomainSize[tmpForDomainSize.length - 1]).append("|");
                line = "";
                for (Integer l : getDomain()) {
                    int size = 0;
                    if (l.intValue() != 0) {
                        size = (int) Math.log10(l.intValue());
                    }
                    sb.append(l).append(tmpForMaxCost[tmpForMaxCost.length - 1]);
                    line += tmpLineForMaxCost;
                }
                line += tmpLineForDomainSize;
                line += "-";
                sb.append("\n").append(line).append("\n");

                for (Integer dj : getDomainOf(i)) {
                    boolean first = true;
                    for (Integer di : getDomainOf(j)) {
                        final int constraintCost = (int) getConstraintCost(i, di, j, dj);
                        int sizeDomain = 0;
                        if (dj != 0) {
                            sizeDomain = (int) Math.log10(dj);
                        }
                        int sizeCons = 0;
                        if (constraintCost != 0) {
                            sizeCons = (int) Math.log10(constraintCost);
                        }
                        if (first) {
                            sb.append(dj).append(tmpForDomainSize[tmpForDomainSize.length - sizeDomain - 2]).append("|");
                            first = false;
                        }
                        sb.append(constraintCost).append(tmpForMaxCost[tmpForMaxCost.length - sizeCons - 1]);
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
    }

    private void constraintsToString(StringBuilder sb) {
        int maxVariables = getNumberOfVariables();
        maxVariables = (int) Math.log10(maxVariables);
        String tmp[] = new String[maxVariables + 2];
        String tmpLine = "";
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = "";
        }
        String line = "";
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < i; j++) {
                tmp[i] += " ";
            }
            tmpLine += "-";
        }
        sb.append("Table Of Constraints:\n");
        sb.append(tmp[tmp.length - 1]).append("|");
        line += tmpLine;
        line += "-";

        for (int l = 0; l < getNumberOfVariables(); l++) {
            int size = 0;
            if (l != 0) {
                size = (int) Math.log10(l);
            }
            sb.append(l).append(tmp[maxVariables - size + 1]);
            line += tmpLine;
        }

        sb.append("\n").append(line).append("\n");

        for (int l = 0; l < getNumberOfVariables(); l++) {
            int size = 0;
            if (l != 0) {
                size = (int) Math.log10(l);
            }
            sb.append(l).append(tmp[maxVariables - size]).append("|");
            for (int k = 0; k < getNumberOfVariables(); k++) {
                if (isConstrained(k, l)) {
                    sb.append("1").append(tmp[tmp.length - 1]);
                } else {
                    sb.append("0").append(tmp[tmp.length - 1]);
                }
            }
            sb.append("\n");
        }

        sb.append("\n");
    }

    protected int calcId(int i, int j) {
        return i * numvars + j;
    }

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2
     * operation cost: o(d^2)cc
     */
    @Override
    public boolean isConstrained(int var1, int var2) {
        return neighbores.get(var1).contains(var2);
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    @Override
    public boolean isConsistent(int var1, int val1, int var2, int val2) {
        return getConstraintCost(var1, val1, var2, val2) == 0;
    }

    /**
     * return the domain size of the variable var
     * @param var
     * @return
     */
    @Override
    public int getDomainSize(int var) {
        return getDomainOf(var).size();
    }

    /**
     * @return this problem metadata
     */
    @Override
    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * @param var
     * @return all the variables that costrainted with the given var 
     */
    @Override
    public Set<Integer> getNeighbors(int var) {

        Set<Integer> ng = null;
        immutableNeighborsLock.readLock().lock();
        try {
            ng = immutableNeighbores.get(var);
        } finally {
            immutableNeighborsLock.readLock().unlock();
        }

        if (ng == null) {
            immutableNeighborsLock.writeLock().lock();
            try {
                Set<Integer> l = this.neighbores.get(var);
                ng = Collections.unmodifiableSet(l);
                immutableNeighbores.put(var, ng);
            } finally {
                immutableNeighborsLock.writeLock().unlock();
            }
        }
        
        return ng;
    }

    @Override
    public int getConstraintCost(int var, int val, Assignment ass) {
        int sum = 0;
        for (Integer av : ass.assignedVariables()) {
            sum += getConstraintCost(var, val, av, ass.getAssignment(av));
        }

        return sum;
    }

    abstract public void setConstraintCost(int var1, int val1, int var2, int val2, int cost);

    public ImmutableSet<Integer> getDomain() {
        return domain;
    }

    @Override
    public int getNumberOfVariables() {
        return numvars;
    }

    public void initialize(ProblemType type, int numberOfVariables, Set<Integer> domain) {
        this.domain = new ImmutableSet<Integer>(domain);
        this.numvars = numberOfVariables;
        this.neighbores = new HashMap<Integer, Set<Integer>>();
        for (int i = 0; i < numvars; i++) {
            neighbores.put(i, new HashSet<Integer>());
        }
        this.type = type;
        _initialize();
    }

    @Override
    public ProblemType type() {
        return type;
    }

    protected abstract void _initialize();

    /**
     * we are not yet supports seperated domains but when we do - this function should be useful
     */
    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return domain;
    }
}
