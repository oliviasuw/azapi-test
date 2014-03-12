package bgu.dcr.az.api;

import bgu.dcr.az.anop.alg.WhenReceived;
import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.exp.RepeatedCallingException;
import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.cp.CPAgentController;
import bgu.dcr.az.mas.cp.CPData;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Agent is the main building block for a CP algorithms, it includes the
 * algorithms main logic. This is The base class of SimpleAgent.
 *
 * The agents have only one entry point and one exit point Entry point: the
 * function start() Exit point: the exit point is not accessed directly instead
 * you can call one of the finish*(*) functions or call panic
 *
 */
public abstract class Agent extends Agt0DSL {

    /**
     * the name for the system termination message the system termination
     * message is getting sent only by the abstract agent
     */
    public static final String SYS_TERMINATION_MESSAGE = "__TERMINATE__";
    private int id; //The Agent ID
    private CPAgentController controller; //This Agent Controller
    private ImmutableProblem prob; // The Agent Local Problem
    private boolean finished = false; //The Status of the current Agent - TODO: TRANSFORM INTO A STATUS ENUM SO WE CAN BE ABLE TO QUERY THE AGENT ABOUT IT CURRENT STATUS
    private Message currentMessage = null; //The Current Message (The Last Message That was taken from the mailbox)
    private PlatformOps pops; //Hidden Platform Operation 
    private long[] ccCount;
    private long[] messageCount;
    private SendMediator sender;

    @Override
    public String toString() {
        final String prefix = "Agent " + (getId() < 10 ? "00" : getId() < 100 ? "0" : "") + getId();

        return prefix + "@" + getClass().getSimpleName();
    }

    /**
     * create a default agent - this agent will have id = -1 so you must
     * reassign it
     */
    public Agent() {
        this.id = -1;
        this.pops = new PlatformOps();

    }

    /**
     * report to statistic collector
     *
     * @param args
     * @return
     */
    protected ReportMediator report(Object... args) {
        return new ReportMediator(args, this, controller);
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
    protected ImmutableProblem getProblem() {
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
    public int getId() {
        return id;
    }

    /**
     * log something inside this agent log
     *
     * @param what
     */
    protected void log(String what) {
        controller.log(id, what);
    }

    protected void logIf(boolean predicate, String what) {
        if (predicate) {
            log(what);
        }
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
            controller.reportNoSolution();
        } else {
            controller.assignAll(ans);
        }
        terminate();
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
        if (!finished) {
            finished = true;
        }
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
        controller.assign(getId(), currentAssignment);
    }

    /**
     * remove the submitted current assignment
     */
    protected void unassign() {
        controller.unassign(id);
    }

    /**
     * @return the last submitted assignment will throw InvalideValueException
     * if no assignment was submitted
     */
    protected Integer getAssignment() {
        Integer assignment = controller.getAssignment(id);
        if (assignment != null) {
            return assignment;
        }

        throw new InvalidValueException("Agent called 'getSubmitedCurrentAssignment' before he ever called 'submitCurrentAssignment'");
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
        return (a == null ? Integer.MAX_VALUE : a.calcCost(prob));
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
        return finished;
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
    @WhenReceived(Agent.SYS_TERMINATION_MESSAGE)
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
    private void terminate() {
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
    public final Message setCurrentMessage(Message currentMessage) {
        this.currentMessage = currentMessage;
        messageCount[id]++;
        return beforeMessageProcessing(currentMessage);
    }

    /**
     * this class contains all the "hidden but public" methods, because the user
     * should extend the agent class all the "platform" operations can be called
     * mistakenly by him, instead of making those operations private and then
     * access them via reflection - which will create a decrease in the
     * performance - we just hide them in this inner class. One private object
     * of this class are held by each agent. In order for the platform to obtain
     * this instance it uses another inner class 'PlatformOperationsExtractor'
     * this class contains a static method that extracts the private field -
     * because it's also defined inside the agent it doesn't have to use
     * reflection to do so.
     */
    public class PlatformOps {

        private int numberOfSetIdCalls = 0;
        private Map metadata = new HashMap();

        public ImmutableProblem getProblem() {
            return prob;
        }

        public Map getMetadata() {
            return metadata;
        }

        /**
         * set the agent id - this method is called by the execution environment
         * to set the agent id and should not be called by hand / by an
         * algorithm implementer this function should only be called once and
         * will throw Repeated Calling Exception upon repeated calls.
         *
         * ** if you need to change the mail group do it before setting the id
         * as setting the id will register the agent to the mailer with it known
         * mail group key
         *
         * @param id
         * @param controller
         * @param execution
         */
        public void initialize(int id, CPAgentController controller, Execution<CPData> execution) {
            numberOfSetIdCalls++;
            if (numberOfSetIdCalls != 1) {
                throw new RepeatedCallingException("you can only call setId once.");
            }

            Agent.this.id = id;
            Agent.this.controller = controller;
            Agent.this.prob = new AgentProblem();
            Agent.this.ccCount = execution.data().getCcCount();
            Agent.this.messageCount = execution.data().getMessagesCount();
            Agent.this.sender = new SendMediator(Agent.this, controller);
        }

    }

    /**
     * See documentation of PlatformOps.
     */
    public static class PlatformOperationsExtractor {

        /**
         * extracting the hidden Platform Operations object from the given
         * agent.
         *
         * @param a
         * @return
         */
        public static PlatformOps extract(Agent a) {
            return a.pops;
        }
    }

    /**
     * this is a wrap on the given problem - each agent poses a wrap like this
     * instead of the actual problem
     */
    public class AgentProblem implements ImmutableProblem {

        ConstraintCheckResult queryTemp = new ConstraintCheckResult();

        public int getAgentId() {
            return Agent.this.getId();
        }

        @Override
        public int getNumberOfVariables() {
            return controller.getGlobalProblem().getNumberOfVariables();
        }

        @Override
        public ImmutableSet<Integer> getDomainOf(int var) {
            return controller.getGlobalProblem().getDomainOf(var);
        }

        @Override
        public int getConstraintCost(int var1, int val1) {
            controller.getGlobalProblem().getConstraintCost(getAgentId(), var1, val1, queryTemp);

            ccCount[id] += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        @Override
        public int getConstraintCost(int var1, int val1, int var2, int val2) {
            controller.getGlobalProblem().getConstraintCost(getAgentId(), var1, val1, var2, val2, queryTemp);

            ccCount[id] += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        @Override
        public String toString() {
            return controller.getGlobalProblem().toString();
        }

        @Override
        public int getDomainSize(int var) {
            return controller.getGlobalProblem().getDomainSize(var);
        }

        @Override
        public HashMap<String, Object> getMetadata() {
            return controller.getGlobalProblem().getMetadata();
        }

        @Override
        public Set<Integer> getNeighbors(int var) {
            return controller.getGlobalProblem().getNeighbors(var);
        }

        @Override
        public boolean isConsistent(int var1, int val1, int var2, int val2) {
            controller.getGlobalProblem().getConstraintCost(getAgentId(), var1, val1, var2, val2, queryTemp);

            ccCount[id] += queryTemp.getCheckCost();
            return queryTemp.getCost() == 0;
        }

        @Override
        public boolean isConstrained(int var1, int var2) {
            return controller.getGlobalProblem().isConstrained(var1, var2);
        }

        /**
         * @return the type of the problem
         */
        @Override
        public ProblemType type() {
            return controller.getGlobalProblem().type();
        }

        @Override
        public int getConstraintCost(Assignment ass) {
            controller.getGlobalProblem().getConstraintCost(getAgentId(), ass, queryTemp);

            ccCount[id] += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        @Override
        public int calculateCost(Assignment a) {
            controller.getGlobalProblem().calculateCost(getAgentId(), a, queryTemp);

            ccCount[id] += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }
    }
}
