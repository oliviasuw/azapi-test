package bgu.dcr.az.dcr.api;

import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.execs.sim.nest.ContinuationMediator;
import bgu.dcr.az.execs.sim.net.Message;
import static bgu.dcr.az.execs.sim.Agt0DSL.range;
import bgu.dcr.az.dcr.api.annotations.Algorithm;
import bgu.dcr.az.dcr.api.annotations.WhenReceived;
import bgu.dcr.az.dcr.api.problems.ImmutableProblem;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.dcr.execution.manipulators.AgentManipulator;
import bgu.dcr.az.dcr.util.ImmutableSet;
import bgu.dcr.az.execs.exceptions.PanicException;
import bgu.dcr.az.execs.sim.Agent;
import bgu.dcr.az.execs.sim.SimulatedMachine;
import static bgu.dcr.az.execs.sim.Agt0DSL.INFINITY_COST;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent is the main building block for a CP algorithms, it includes the
 * algorithms main logic. This is The base class of SimpleAgent.
 *
 * The agents have only one entry point and one exit point Entry point: the
 * function start() Exit point: the exit point is not accessed directly instead
 * you can call one of the finish*(*) functions or call panic
 *
 */
@Algorithm("BASE_CP_AGENT")
public abstract class CPAgent<P extends ImmutableProblem> extends Agent {

    private static final Map<Class, AgentManipulator> manipulators = new ConcurrentHashMap<>();

    /**
     * the name for the system termination message the system termination
     * message is getting sent only by the abstract agent
     */
    public static final String SYS_TERMINATION_MESSAGE = "__TERMINATE__";
    private P prob; // The Agent Local Problem
    private Message currentMessage = null; //The Current Message (The Last Message That was taken from the mailbox)
    private SendMediator sender;
    private SimulatedMachine controller;
    private CPSolution solution;

    public final ContinuationMediator exec(CPAgent agent) {
        return exec(agent, null);
    }

    public final ContinuationMediator exec(CPAgent agent, String contextId) {
        try {
            return controller.nest(agent, contextId);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return String.format("Agent %03d@%s", getId(), getClass().getSimpleName());
    }

    /**
     * create a default agent - this agent will have id = -1 so you must
     * reassign it
     */
    public CPAgent() {
        super(new Agent.Internals() {

            CPAgent a;
            AgentManipulator manipulator;

            @Override
            public void initialize(int id, SimulatedMachine ac, Agent a, Map<String, String> args) {
                this.a = (CPAgent) a;
                this.a.controller = ac;
                this.manipulator = manipulators.get(a.getClass());
                if (this.manipulator == null) {
                    this.manipulator = AgentManipulator.lookup(this.a.getClass());
                    manipulators.put(a.getClass(), manipulator);
                }

                final CPData data = (CPData) ac.getSimulation().data();
                this.a.solution = data.currentSolution();
                this.a.prob = data.getProblem().createLocalProblem(id);

                for (Map.Entry<String, String> c : args.entrySet()) {
                    try {
                        manipulator.configureProperty(a, c.getKey(), c.getValue());
                    } catch (ConfigurationException ex) {
                        System.err.println("Agent " + a + " could not configure property " + c.getKey());
                    }
                }
            }

            @Override
            public void start() {
                a.start();
            }

            @Override
            public void handleIdle() {
                a.onIdleDetected();
            }

            @Override
            public void handleMessage(Message m) {
                m = a.beforeMessageProcessing(m);

                if (m != null) {
                    a.setCurrentMessage(m);
                    manipulator.handle(a, m.getName(), m.getArgs());
                }
            }

            @Override
            public void handleTick(int tick) {
                a.onMailBoxEmpty();
            }
        });
    }

    /**
     * override this function in case you want to make some action every time
     * before sending a message this is a great place to write logs, attach
     * timestamps to the message etc.
     *
     * @param m
     */
    protected void beforeMessageSending(Message m) {
        //do nothing - derived classes can implement this if they want
    }

    /**
     * the agent is an message driven creature - he works only if he received
     * any message - this is the message that the agent currently processing =
     * the last message taken from the mailbox
     *
     * @return
     */
    protected Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * @return the problem currently being worked on each agent has its own
     * unique instance of the problem (based on the global problem that was
     * given to the simulator) and he manages it differently - so if you are
     * building tools that have to be sent with the mailer to other agents don't
     * include the agent's problem in them as a field.
     */
    protected P getProblem() {
        return prob;
    }

    /**
     * @return set of variables that are constrainted with this agent variable
     */
    protected Set<Integer> getNeighbors() {
        return prob.getNeighbors(getId());
    }

    /**
     * @return the id of this agent. In simple algorithms this ID is the
     * variable that the agent is "handling" starting from 0 to number of
     * variables - 1
     */
    @Override
    public int getId() {
        return super.getId();
    }

    /**
     * log something inside this agent log
     *
     * @param what
     */
    protected void log(String what) {
        System.out.println(this.toString() + ": " + what);
    }

    protected void logIf(boolean predicate, String what) {
        if (predicate) {
            log(what);
        }
    }

    /**
     * stop execution - returning the given cost, will cause a TERMINATION
     * message to be sent to all other agents. The need for this function is to
     * submit a solution quality for algorithms that not tracking the best
     * assignment but only the best cost
     *
     * @param cost
     */
    protected void finishWithSolutionCost(int cost) {
        solution.setFinalCost(cost);
        terminateSimulation();
    }

    /**
     * stop execution - returning the given assignment, will cause a TERMINATION
     * message to be sent to all other agents, if ans is null there is no
     * solution
     *
     * @param ans
     */
    protected void finish(Assignment ans) {
        if (ans == null) {
            solution.setStateNoSolution();
        } else {
            solution.assignAll(ans);
        }

        terminateSimulation();
    }

    /**
     * stop the execution (send TERMINATION to all agents) without solution -
     * this method should be used in csp problem as it make sense there. This is
     * the same as calling finish(null)
     */
    protected void finishWithNoSolution() {
        finish(null);
    }

    /**
     * stop execution of <b> current </b> agent - will not affect other agents
     * most of the times the desired function to call is
     * finish(current-assignment) or finish(full-assignment) this function is
     * here so that you can implement your own shutdown mechanism
     */
    protected void finish() {
        terminate();
    }

    /**
     * call this function when an agent is done and want to report its
     * assignment upon finishing
     *
     * @param currentAssignment
     */
    protected void finish(int currentAssignment) {
        assign(currentAssignment);
        finish();
    }

    /**
     * the agent can submit its assignment so that when the algorithm is finish
     * running (happened when all agents call finish) this will be the
     * assignment to be accumulated - if you want to re-assign a new value you
     * don't have to call unSubmitCurrentAssignment, you can just call this
     * function again with the new value
     *
     * @param currentAssignment the assignment to submit
     */
    protected void assign(int currentAssignment) {
        solution.assign(getId(), currentAssignment);
    }

    /**
     * remove the submitted current assignment
     */
    protected void unassign() {
        solution.unassign(getId());
    }

    /**
     * @return the last submitted assignment will throw InvalideValueException
     * if no assignment was submitted (if this behavior is undesired check {@link #getAssignment(int)
     * }
     */
    protected Integer getAssignment() {
        Integer assignment = solution.assignmentOf(getId());
        if (assignment != null) {
            return assignment;
        }

        throw new PanicException("Agent called 'getSubmitedCurrentAssignment' before he ever called 'submitCurrentAssignment'");
    }

    /**
     * @return the last submitted assignment, will return the given default
     * value if no assignment fount
     */
    protected Integer getAssignment(int defaultValue) {
        Integer assignment = solution.assignmentOf(getId());
        if (assignment != null) {
            return assignment;
        }

        return defaultValue;
    }

    /**
     * this function called once on each agent when the algorithm is started
     */
    public abstract void start();

    /**
     * @return true if this is the first agent current implementation only
     * checks if this agent's id is 0 but later implementations can use variable
     * arranger that can change the first agent's id
     */
    public boolean isFirstAgent() {
        return this.getId() == 0;
    }

    /**
     * @return true if this is the last agent current implementation just checks
     * if this agent's id +1 is num_of_vars but later implementations can use
     * variable arranger that can change the last agent's id
     */
    protected boolean isLastAgent() {
        return this.getId() + 1 == getNumberOfVariables();
    }

    /**
     * same as calling a.calcCost(getProblem()); accept - if a is null returns
     * infinity (= Integer.MAX_VALUE)
     *
     * @param a
     * @return
     */
    protected int costOf(Assignment a) {
        return (a == null ? INFINITY_COST : a.calcCost(prob));
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1<-val1 while var2=val2 in the current
     * problem
     */
    public int getConstraintCost(int var1, int val1, int var2, int val2) {
        return getProblem().getConstraintCost(var1, val1, var2, val2);
    }

    /**
     * @param var1
     * @param val1
     * @return the unary cost of assigning var1<-val1 in the current problem
     */
    public int getConstraintCost(int var1, int val1) {
        return getProblem().getConstraintCost(var1, val1);
    }

    /**
     * @return the number of variable in the current problem
     */
    public int getNumberOfVariables() {
        return getProblem().getNumberOfVariables();
    }

    /**
     * @param var
     * @return the domain of some variable in the current problem
     */
    protected ImmutableSet<Integer> getDomainOf(int var) {
        return getProblem().getDomainOf(var);
    }

    /**
     * @return this agents full domain - as immutable set - if you need to
     * change your domain- copy this set and then change your copy :
     * HashSet<Integer> currentDomain = new HashSet<Integer>(getDomain());
     */
    public ImmutableSet<Integer> getDomain() {
        return getProblem().getDomainOf(getId());
    }

    /**
     * same as getDomain().size()
     *
     * @return
     */
    public int getDomainSize() {
        return getDomainOf(getId()).size();
    }

    /**
     * @param var1
     * @param var2
     * @return true if var1 is constrained with var2 which means : there is val1
     * in domainOf[var1] and val2 in domainOf[var2] where
     * getConstraintCost(val1, var1, var2, val2) != 0
     */
    public boolean isConstrained(int var1, int var2) {
        return getProblem().isConstrained(var1, var2);
    }

    /**
     * @return true if this agent called one of the finish methods (or panic.. )
     */
    public boolean isFinished() {
        return terminated();
    }

    /**
     * send the given message+arguments to all other agents except the sender
     * (read the method send javadoc for more details about sending the message)
     *
     * @param msg
     * @param args
     */
    protected void broadcast(String msg, Object... args) {
        send(msg, args).broadcast();
    }

    /**
     * sends a new message the message should have a name and any number of
     * arguments the message which will be sent here will be received by an
     * agent in the method that defines
     *
     * @WhenReceived with the name of the message (case sensitive!) and the
     * arguments will be inserted to the parameters of that method
     *
     * usage: send("MESSAGE_NAME", ARG1, ARG2, ..., ARGn).to(OTHER_AGENT_ID)
     *
     * @param msg the message name
     * @param args the list (variadic) of arguments that belongs to this message
     * @return continuation class
     */
    protected SendMediator send(String msg, Object... args) {
        sender.setArgs(args);
        sender.setMessageName(msg);
        return sender;
    }

    /**
     * a callback which is called when idle detected - this is the place to
     * finish the algorithm or revive from idle
     */
    public void onIdleDetected() {
        throw new UnsupportedOperationException("unexpected idle reached. If you are writing an algorithm that expect idle state you must implement Agent.onIdleDetected method in agent class " + getClass().getSimpleName());
    }

    /**
     * a callback which is called (only when running in synchronized mode) just
     * before the next tick (when the agent finish handling all its messages)
     */
    public void onMailBoxEmpty() {
        throw new UnsupportedOperationException("if you are running a Synchronized Search you must implements Agent.onMailBoxEmpty method");
    }

    /**
     * this function is called when a SYS_TERMINATION Message Arrived -> it just
     * calls finish on the agent, you can override it to make your own
     * termination handling but dont forgot to reassign it to the termination
     * message. you should override it as follows:
     * <pre>
     * {@code
     *
     * @WhenReceived(Agent.SYS_TERMINATION_MESSAGE) protected void
     * handleTermination() { //your code here... }
     * }
     * </pre>
     *
     */
    @WhenReceived(CPAgent.SYS_TERMINATION_MESSAGE)
    public void handleTermination() {
        finish();
    }

    /**
     * Note: the concept 'system time' only exists in synchronized execution
     *
     * @return the number of ticks passed since the algorithm start (first tick
     * is 0), you can read about the definition of tick in agent zero manual
     */
    public long getSystemTimeInTicks() {
        return controller.getTickNumber();
    }

    /**
     * send termination to all agents
     */
    private void terminateSimulation() {
        send(SYS_TERMINATION_MESSAGE).toAll(range(0, getNumberOfVariables() - 1));
    }

    /**
     * you can override this method to perform preprocessing before messages
     * arrive to their functions you can change the message or even return
     * completly other one - if you will return null the message is rejected and
     * dumped.
     *
     * @param msg
     * @return
     */
    protected Message beforeMessageProcessing(Message msg) {
        return msg;
    }

    /**
     * if this method return null the message is rejected and should be dumped.
     *
     * @param currentMessage
     * @return
     */
    public final void setCurrentMessage(Message currentMessage) {
        this.currentMessage = currentMessage;
//        if (!controller.isControlling(currentMessage.getSender()) && !SYS_TERMINATION_MESSAGE.equals(currentMessage.getName())) {
//            messageCount[controller.pid()]++;
//        }
    }

}
