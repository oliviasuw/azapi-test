package bgu.dcr.az.dcr.api.problems;

import bgu.dcr.az.dcr.api.problems.constraints.KAryConstraint;
import bgu.dcr.az.execs.sim.Agt0DSL;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.problems.constraints.BinaryConstraint;
import bgu.dcr.az.dcr.api.problems.cpack.ConstraintsPackage;
import bgu.dcr.az.dcr.util.ImmutableSet;
import bgu.dcr.az.execs.exps.exe.SimulationConfiguration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract class for problems that should let you build any type of problem
 *
 * @author guyafe, edited by bennyl
 */
public class Problem implements ImmutableProblem {

    private final HashMap<String, Object> metadata = new HashMap<>();
    protected int numagents;
    protected ImmutableSetOfIntegers[] domain;
    protected ConstraintsPackage constraints;
    protected ProblemType type;
    protected int maxCost = 0;
    protected boolean singleDomain = true;
    private SimulationConfiguration.Builder initialConfiguration;
    private long[] ccCount;

    public SimulationConfiguration.Builder getInitialConfiguration() {
        initialConfiguration.numAgents(getNumberOfVariables());
        initialConfiguration.numMachines(getNumberOfVariables());
        return initialConfiguration;
    }

    public void resetCC_Count() {
        ccCount = new long[numagents];
    }

    public long[] getCC_Count() {
        return ccCount;
    }

    /**
     *
     * @param agentId
     * @return the set of variables owned by a given agent
     */
    public int[] getVariablesOwnedByAgent(int agentId) {
        return initialConfiguration.agentsInMachine(agentId);
    }

    public void assignVariablesToAgent(int aid, int... variables) {
        initialConfiguration.withAgentsInMachine(aid, variables);
    }

    public LocalProblem createLocalProblem(int variable) {
        return new LocalProblem(variable, this);
    }

    /**
     * Changes current allocation of variables to agents. After performing this
     * operation the agent with given id will own a given set of variables. In
     * case that amount of variables equals the amount of agents (only
     * one-to-one variable allocation allowed) an Exception will be thrown.
     *
     * @param agentId
     * @param vars
     */
    public void setVariablesOwnedByAgent(int agentId, int... vars) {
        if (getNumberOfAgents() == getNumberOfVariables()) {
            throw new RuntimeException("Since the amount of agents equals to amount of variables, only One-T-One variable allocation allowed");
        }

        initialConfiguration.withAgentsInMachine(agentId, vars);
    }

    @Override
    public String toString() {
        return ProblemPrinter.toString(this);
    }

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2 operation
     * cost: o(d^2)cc
     */
    @Override
    public boolean isConstrained(int var1, int var2) {
        return getNeighbors(var1).contains(var2);
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
     *
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
     * @return all the variables that are constrained with the given variable,
     * even if there is an unary constraint for this variable the variable
     * itself will never be one of its own neighbors
     *
     */
    @Override
    public Set<Integer> getNeighbors(int var) {
        return constraints.getNeighbores(var);
    }

    public ImmutableSet<Integer> getDomain() {
        if (!singleDomain) {
            throw new UnsupportedOperationException("calling get domain on a problem with domain that is unique to each variable is unsupported - call  getDomainOf(int) instaed.");
        }
        return domain[0];
    }

    @Override
    public int getNumberOfVariables() {
        return domain.length;
    }

    public int getNumberOfAgents() {
        return numagents;
    }

    protected void initialize(ProblemType type, List<? extends Set<Integer>> domain, boolean singleDomain, int numberOfAgents) {
        if (domain.size() >= numberOfAgents) {
            this.singleDomain = singleDomain;
            this.domain = ImmutableSetOfIntegers.arrayOf(domain);
            setNumberOfAgents(numagents);
            this.type = type;
            this.constraints = type.newConstraintPackage(domain.size(), domain.stream().max((x, y) -> x.size() - y.size()).get().size());
        } else {
            throw new RuntimeException("The amount of agents must be less/equal than the amount of variables");
        }
    }

    public void setNumberOfAgents(int numagents) {
        this.numagents = numagents;
        resetCC_Count();
    }

    /**
     * initialize the problem with multiple domains the number of variables is
     * the domain.size()
     *
     * @param type the type of the problem
     * @param domains list of domains for each agent - this list also determines
     * @param numberOfAgents amount of agents (must be less\equal to amount of
     * variables) otherwise an exception will be thrown the number of variables
     * that will be domains.size
     */
    public void initialize(ProblemType type, List<? extends Set<Integer>> domains, int numberOfAgents) {
        initialize(type, domains, false, numberOfAgents);
    }

    /**
     * initialize the problem with a single domain
     *
     * @param type the problem type
     * @param numberOfVariables number of variables in this problem
     * @param domain the domain for all the variables.
     * @param numberOfAgents amount of agents (must be less\equal to amount of
     * variables) otherwise an exception will be thrown
     */
    public void initialize(ProblemType type, int numberOfVariables, Set<Integer> domain, int numberOfAgents) {
        initialize(type, ImmutableSetOfIntegers.repeat(domain, numberOfVariables), true, numberOfAgents);
    }

    /**
     * initialize the problem with a single domain that its values are
     * 0..1-domainSize
     *
     * @param type
     * @param numberOfVariables
     * @param domainSize
     * @param numberOfAgents amount of agents (must be less\equal to amount of
     * variables) otherwise an exception will be thrown
     */
    public void initialize(ProblemType type, int numberOfVariables, int domainSize, int numberOfAgents) {
        initialize(type, numberOfVariables, new HashSet<>(Agt0DSL.range(0, domainSize - 1)), numberOfAgents);
    }

    protected void initialize(ProblemType type, List<? extends Set<Integer>> domain, boolean singleDomain) {
        initialize(type, domain, singleDomain, domain.size());
    }

    /**
     * initialize the problem with multiple domains the number of variables is
     * the domain.size()
     *
     * @param type the type of the problem
     * @param domains list of domains for each agent - this list also determines
     * the number of variables that will be domains.size
     */
    public void initialize(ProblemType type, List<? extends Set<Integer>> domains) {
        initialize(type, domains, false);
    }

    /**
     * initialize the problem with a single domain
     *
     * @param type the problem type
     * @param numberOfVariables number of variables in this problem
     * @param domain the domain for all the variables.
     */
    public void initialize(ProblemType type, int numberOfVariables, Set<Integer> domain) {
        initialize(type, ImmutableSetOfIntegers.repeat(domain, numberOfVariables), true);
    }

    /**
     * initialize the problem with a single domain that its values are
     * 0..1-domainSize
     *
     * @param type
     * @param numberOfVariables
     * @param domainSize
     */
    public void initialize(ProblemType type, int numberOfVariables, int domainSize) {
        initialize(type, numberOfVariables, new HashSet<>(Agt0DSL.range(0, domainSize - 1)));
    }

    /**
     * @return the type of the problem
     */
    @Override
    public ProblemType type() {
        return type;
    }

    /**
     * return the domain that belongs to variable var
     */
    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return domain[var];
    }

    @Override
    public int getConstraintCost(Assignment ass) {
        throw new UnsupportedOperationException("Not supported without providing owner. Please use getConstraintCost(int owner, Assignment ass)");
    }

    @Override
    public int calculateCost(Assignment a) {
        throw new UnsupportedOperationException("Not supported when not accessed from inside of an agent code - please use calculateGlobalCost");
    }

    /**
     * this class is required to allow array of this type as java cannot create
     * an array of generic types and we want to avoid uneccecery casting
     */
    protected static class ImmutableSetOfIntegers extends ImmutableSet<Integer> {

        public ImmutableSetOfIntegers(Collection<Integer> data) {
            super(data);
        }

        public static ImmutableSetOfIntegers[] arrayOf(List<? extends Set<Integer>> of) {
            ImmutableSetOfIntegers[] a = new ImmutableSetOfIntegers[of.size()];

            int i = 0;
            for (Set<Integer> o : of) {
                a[i++] = new ImmutableSetOfIntegers(o);
            }

            return a;
        }

        public static List<ImmutableSetOfIntegers> repeat(Set<Integer> set, int times) {
            ImmutableSetOfIntegers[] ret = new ImmutableSetOfIntegers[times];
            ImmutableSetOfIntegers is = new ImmutableSetOfIntegers(set);
            for (int i = 0; i < ret.length; i++) {
                ret[i] = is;
            }

            return Arrays.asList(ret);
        }
    }

    /**
     * tries to add new kary constraint - as can be seen from the owner eyes
     * replaces the k-ary constraint if it exists
     *
     * @param owner
     * @param constraint
     */
    public void setConstraint(int owner, KAryConstraint constraint) {
        constraints.setConstraint(owner, constraint);
    }

    /**
     * add into the existing constrains (if a different constraint can be
     * applied on the given participients then this constraint cost will get
     * added to it)
     *
     * @param owner
     * @param constraint
     */
    public void addConstraint(int owner, KAryConstraint constraint) {
        constraints.addConstraint(owner, constraint);
    }

    /**
     * symmetrically adding the constraint to all of the participants (if a
     * different constraint can be applied on the given participients then this
     * constraint cost will get added to it)
     *
     * @param constraint
     */
    public void addConstraint(KAryConstraint constraint) {
        for (int participant : constraint.getParicipients()) {
            constraints.addConstraint(participant, constraint);
        }
    }

    /**
     * symmetrically setting the constraint to all of the participants (if a
     * different constraint can be applied on the given participients then this
     * constraint cost will override it)
     *
     * @see setConstraintCost
     *
     * @param constraint
     */
    public void setConstraint(KAryConstraint constraint) {
        for (int participant : constraint.getParicipients()) {
            constraints.setConstraint(participant, constraint);
        }
    }

    /**
     * set u's constraint for values of v
     *
     * @param constraint
     */
    public void setConstraint(int u, int v, BinaryConstraint constraint) {
        constraints.setConstraint(u, u, v, constraint);
    }

    public void setConstraintCost(int owner, int x1, int v1, int x2, int v2, int cost) {
        constraints.setConstraintCost(owner, x1, v1, x2, v2, cost);
    }

    public void setConstraintCost(int owner, int x1, int v1, int cost) {
        constraints.setConstraintCost(owner, x1, v1, cost);
    }

    public void setConstraintCost(int x1, int v1, int x2, int v2, int cost) {
        constraints.setConstraintCost(x1, x1, v1, x2, v2, cost);
    }

    public void setConstraintCost(int x1, int v1, int cost) {
        constraints.setConstraintCost(x1, x1, v1, cost);
    }

    public void getConstraintCost(int owner, int x1, int v1, ConstraintCheckResult result) {
        constraints.getConstraintCost(owner, x1, v1, result);
        ccCount[owner] += result.getCheckCost();
    }

    public void getConstraintCost(int owner, int x1, int v1, int x2, int v2, ConstraintCheckResult result) {
        constraints.getConstraintCost(owner, x1, v1, x2, v2, result);
        ccCount[owner] += result.getCheckCost();
    }

    public int getConstraintCost(int owner, int x1, int v1) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(owner, x1, v1, result);
        return result.getCost();
    }

    public int getConstraintCost(int owner, int x1, int v1, int x2, int v2) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(owner, x1, v1, x2, v2, result);
        return result.getCost();
    }

    @Override
    public int getConstraintCost(int x1, int v1) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(x1, x1, v1, result);
        return result.getCost();
    }

    @Override
    public int getConstraintCost(int x1, int v1, int x2, int v2) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(x1, x1, v1, x2, v2, result);
        return result.getCost();
    }

    public void getConstraintCost(int owner, Assignment k, ConstraintCheckResult result) {
        constraints.getConstraintCost(owner, k, result);
        ccCount[owner] += result.getCheckCost();
    }

    public int getConstraintCost(int owner, Assignment k) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(owner, k, result);
        ccCount[owner] += result.getCheckCost();
        return result.getCost();
    }

    public void addNeighbor(int to, int neighbor) {
        constraints.addNeighbor(to, neighbor);
    }

    /**
     * calculate the cost of the given assignment - taking into consideration
     * all the constraints that related to it, the method put the return value
     * in an array of 2 [cost, number of constraints checked] that is given to
     * the method as the parameter ans
     *
     * @param owner
     * @param assignment
     * @param result
     */
    public void calculateCost(int owner, Assignment assignment, ConstraintCheckResult result) {
        constraints.calculateCost(owner, assignment, result);
        ccCount[owner] += result.getCheckCost();
    }

    public int calculateGlobalCost(Assignment a) {
        return constraints.calculateGlobalCost(a);
    }

}
